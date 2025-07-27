package boundary.DoctorManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.Doctor;
import enitity.DutySlot;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import utility.FileUtils;
import utility.ImageUtils;
import utility.ReportGenerator;

/**
 *
 * @author Chok Chun Fai
 */
public class DoctorSchedulePanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private DoublyLinkedList<DutySlot> thisWeekSchedule;
    private DoublyLinkedList<DutySlot> nextWeekSchedule;
    private DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;

    // Keep your constants
//    final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//    final String[] SHIFTS = {"Morning (8am - 2pm)", "Evening (2pm - 8pm)"};
    final DoublyLinkedList<String> DAYS = new DoublyLinkedList<>();
    final DoublyLinkedList<String> SHIFTS = new DoublyLinkedList<>();

    /**
     * Creates new form DoctorSchedulePanel
     *
     * @param mainFrame
     */
    public DoctorSchedulePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

        DAYS.insertLast("Monday");
        DAYS.insertLast("Tuesday");
        DAYS.insertLast("Wednesday");
        DAYS.insertLast("Thursday");
        DAYS.insertLast("Friday");
        DAYS.insertLast("Saturday");

        SHIFTS.insertLast("Morning (8am - 2pm)");
        SHIFTS.insertLast("Evening (2pm - 8pm)");

        setupTable(oldScheduleTable, false); // 'false' because it's not editable
        setupTable(newScheduleTable, true);  // 'true' because it IS editable
        runScheduleRolloverCheck(LocalDate.now());
        loadInitialData();
//        hardCordedData();
    }

//        Initialize Hard-coded Data for this week 
    private void hardCordedData() {

        thisWeekSchedule = new DoublyLinkedList<>();
        DutySlot dataSlot1 = new DutySlot("Monday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot2 = new DutySlot("Tuesday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot3 = new DutySlot("Wednesday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot4 = new DutySlot("Thursday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot5 = new DutySlot("Friday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot6 = new DutySlot("Saturday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot7 = new DutySlot("Monday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot8 = new DutySlot("Tuesday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot9 = new DutySlot("Wednesday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot10 = new DutySlot("Thursday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot11 = new DutySlot("Friday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot12 = new DutySlot("Saturday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());

        thisWeekSchedule.insertLast(dataSlot1);
        thisWeekSchedule.insertLast(dataSlot2);
        thisWeekSchedule.insertLast(dataSlot3);
        thisWeekSchedule.insertLast(dataSlot4);
        thisWeekSchedule.insertLast(dataSlot5);
        thisWeekSchedule.insertLast(dataSlot6);
        thisWeekSchedule.insertLast(dataSlot7);
        thisWeekSchedule.insertLast(dataSlot8);
        thisWeekSchedule.insertLast(dataSlot9);
        thisWeekSchedule.insertLast(dataSlot10);
        thisWeekSchedule.insertLast(dataSlot11);
        thisWeekSchedule.insertLast(dataSlot12);
        FileUtils.writeDataToFile("this_week_schedule", thisWeekSchedule);
    }

    private void loadInitialData() {

        // --- 1. Load Data ---
        // Load the master list of all doctors
        masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (masterDoctorList == null) {
            masterDoctorList = new DoublyLinkedList<>(); // Ensure it's not null
        }

        // Load this week's schedule from a file
        thisWeekSchedule = (DoublyLinkedList<DutySlot>) FileUtils.readDataFromFile("this_week_schedule");
        if (thisWeekSchedule == null) {
            thisWeekSchedule = new DoublyLinkedList<>(); // Or create a default empty one
        }

        // Create a unassigned schedule for next week
        nextWeekSchedule = (DoublyLinkedList<DutySlot>) FileUtils.readDataFromFile("next_week_schedule");

        // Initialize the schedule if the file doesn't exist OR if it's empty.
        if (nextWeekSchedule == null || nextWeekSchedule.isEmpty()) {
            nextWeekSchedule = new DoublyLinkedList<>(); // Make sure we have a fresh list
            for (String day : DAYS) {
                for (String shift : SHIFTS) {
                    // This now correctly creates the empty slots
                    nextWeekSchedule.insertLast(new DutySlot(day, shift));
                }
            }
        }

        // --- 2. Populate UI ---
        populateTable(oldScheduleTable, thisWeekSchedule); // Populate "This Week" table
        populateTable(newScheduleTable, nextWeekSchedule); // Populate "Next Week" table

        // Make the "This Week" table non-editable
        oldScheduleTable.setDefaultEditor(Object.class, null);

        // --- 3. Setup JComboBox Editor for the "Next Week" Table ---
        setupDoctorEditor();

        // --- 4. Add Listener to newScheduleTable to Handle Assignments ---
        newScheduleTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column < 1) {
                    return; // Ignore updates to the "Days" column
                }
                String selectedDoctorName = (String) newScheduleTable.getValueAt(row, column);
                String day = (String) newScheduleTable.getValueAt(row, 0);
                String shift = null;

                if (column == 1) {
                    shift = SHIFTS.getFirst().getEntry(); // Morning shift
                } else if (column == 2) {
                    shift = SHIFTS.getLast().getEntry();  // Evening shift
                }

                if (shift == null) {
                    System.out.println("ERROR: Could not determine shift for column " + column);
                    return;
                }

                DutySlot targetSlot = findDutySlot(nextWeekSchedule, day, shift);
                Doctor doctorToAssign = findDoctorByName(selectedDoctorName);

                if (targetSlot != null) {
                    targetSlot.setAssignedDoctor(doctorToAssign);
                    System.out.println("SUCCESS: Assigned " + (doctorToAssign != null ? doctorToAssign.getName() : "Unassigned") + " to " + day + " " + shift);
                } else {
                    System.out.println("ERROR: Could not find DutySlot for Day: [" + day + "], Shift: [" + shift + "]");
                }
            }
        });
    }

    /**
     * Sets up the model and columns for a schedule table. This should only be
     * called once.
     *
     * @param table The JTable to configure.
     * @param isEditable If true, the shift columns will be editable.
     */
    private void setupTable(javax.swing.JTable table, boolean isEditable) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // If the table is editable, allow editing columns 1 and 2.
                return isEditable && column > 0;
            }
        };
        table.setModel(model);

        // Add columns at table.
        model.addColumn("Days / Shifts");
        model.addColumn("Morning (8am - 2pm)");
        model.addColumn("Evening (2pm - 8pm)");
    }

    /**
     * Clears the table and fills it with data from a schedule. This method is
     * safe to call multiple times.
     *
     * @param table The JTable to populate.
     * @param schedule The schedule data to display.
     */
    private void populateTable(javax.swing.JTable table, DoublyLinkedList<DutySlot> schedule) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setRowCount(0);

        int rowIndex = 0;

        for (String day : DAYS) {
            // 1. Add a new, empty row to the model first.
            model.addRow(new Object[model.getColumnCount()]);

            // 2. Set the value for the "Day" column (column 0)
            model.setValueAt(day, rowIndex, 0);

            // 3. Find the assigned doctors for this day
            Doctor morningDoctor = findDoctorForSlot(schedule, day, "Morning (8am - 2pm)");
            Doctor eveningDoctor = findDoctorForSlot(schedule, day, "Evening (2pm - 8pm)");

            // 4. Prepare the text to display
            String morningText = (morningDoctor != null) ? morningDoctor.getName() : "Unassigned";
            String eveningText = (eveningDoctor != null) ? eveningDoctor.getName() : "Unassigned";

            // 5. Set the values for the shift columns (1 and 2)
            model.setValueAt(morningText, rowIndex, 1);
            model.setValueAt(eveningText, rowIndex, 2);

            // 6. Move to the next row for the next iteration
            rowIndex++;
        }

    }

    private void setupDoctorEditor() {
        DoublyLinkedList<String> doctorNames = new DoublyLinkedList<>();

        doctorNames.insertLast("Unassigned");

        for (Pair<String, Doctor> pair : masterDoctorList) {
            if (!pair.getValue().getStatus().equals("Absent") && !pair.getValue().getStatus().equals("Resigned")) {
                doctorNames.insertLast(pair.getValue().getName());
            }
        }

        JComboBox<String> doctorComboBox = new JComboBox<>();
        for (String o : doctorNames) {
            doctorComboBox.addItem(o);
        }

        DefaultCellEditor doctorEditor = new DefaultCellEditor(doctorComboBox);
        TableColumnModel columnModel = newScheduleTable.getColumnModel();

        TableColumn morningColumn = columnModel.getColumn(1);
        TableColumn eveningColumn = columnModel.getColumn(2);

        morningColumn.setCellEditor(doctorEditor);
        eveningColumn.setCellEditor(doctorEditor);
    }

    private void runScheduleRolloverCheck(LocalDate today) {
        // 1. Read the list containing the date string from the file
        DoublyLinkedList<String> dateList = (DoublyLinkedList<String>) FileUtils.readDataFromFile("schedule_info");

        // 2. Check if the list is valid and not empty
        if (dateList == null || dateList.isEmpty()) {
            return;
        }

        // 3. Get the first item from the list and parse it back to a LocalDate
        String savedDateStr = dateList.getFirst().getEntry(); // Assuming your list has a .getFirst() method
        LocalDate scheduleStartDate = LocalDate.parse(savedDateStr);

        // 3. Compare if 'today' is in a later week than the 'scheduleStartDate'
        if (!today.isBefore(scheduleStartDate)) { // If we are past the end of that week
            System.out.println("New week detected! Rolling over schedules...");

            File thisWeekFile = new File("dao/this_week_schedule.bin");
            File nextWeekFile = new File("dao/next_week_schedule.bin");

            LocalDate newScheduleStartDate = scheduleStartDate.plusWeeks(1);
            // Create a new list to hold the updated date string
            DoublyLinkedList<String> newDateList = new DoublyLinkedList<>();
            newDateList.insertLast(newScheduleStartDate.toString());

            // 4. Perform the file operations
            if (nextWeekFile.exists()) {
                // Delete the old "this week" file
                if (thisWeekFile.exists()) {
                    thisWeekFile.delete();
                }
                boolean success = nextWeekFile.renameTo(thisWeekFile);
                System.out.println("Rename successful: " + success); // Good for debugging

                if (success) {
                    // Important: Update the info file with the new week's start date
                    newDateList.insertLast(newScheduleStartDate.toString());
                    FileUtils.writeDataToFile("schedule_info", newDateList);
                }
            }
        }
    }

    // Method to find the right doctor in list
    private Doctor findDoctorForSlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {
        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot.getAssignedDoctor();
            }
        }
        return null;
    }

    // Method to find a schedule slot
    private DutySlot findDutySlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {

        schedule.displayFromFirst(schedule.getFirst());

        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot;
            }
        }
        return null;
    }

    // Helper method to find a Doctor object by their name
    private Doctor findDoctorByName(String name) {
        // If the user selected "Unassigned", assign null.
        if (name == null || name.equals("Unassigned")) {
            return null;
        }
        // Search the master list for a doctor with the matching name.
        for (Pair<String, Doctor> pair : masterDoctorList) {
            Doctor doctor = pair.getValue();
            if (doctor.getName().equals(name)) {
                return doctor; // Found 
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        logoPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        scheduleWrapperPanel = new javax.swing.JPanel();
        oldScheduleLabelPanel = new javax.swing.JPanel();
        oldScheduleLabel = new javax.swing.JLabel();
        oldScheduleBorderPanel = new javax.swing.JPanel();
        oldSchedulePanel = new javax.swing.JScrollPane();
        oldScheduleTable = new javax.swing.JTable();
        newScheduleWrapperPanel = new javax.swing.JPanel();
        newScheduleLabelPanel = new javax.swing.JPanel();
        newScheduleLabel = new javax.swing.JLabel();
        newSchedulePanel = new javax.swing.JScrollPane();
        newScheduleTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Doctor Schedule");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        oldScheduleLabelPanel.setLayout(new java.awt.BorderLayout());

        oldScheduleLabel.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        oldScheduleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        oldScheduleLabel.setText("This Week : ");
        oldScheduleLabelPanel.add(oldScheduleLabel, java.awt.BorderLayout.PAGE_START);

        oldScheduleBorderPanel.setLayout(new java.awt.BorderLayout());

        oldSchedulePanel.setPreferredSize(new java.awt.Dimension(453, 124));

        oldScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        oldScheduleTable.setName("oldScheduleTable"); // NOI18N
        oldScheduleTable.setShowGrid(true);
        oldScheduleTable.getTableHeader().setReorderingAllowed(false);
        oldSchedulePanel.setViewportView(oldScheduleTable);
        oldScheduleTable.getAccessibleContext().setAccessibleName("olsScheduleTable");

        oldScheduleBorderPanel.add(oldSchedulePanel, java.awt.BorderLayout.CENTER);

        newScheduleWrapperPanel.setPreferredSize(new java.awt.Dimension(100, 23));

        newScheduleLabelPanel.setLayout(new java.awt.BorderLayout());

        newScheduleLabel.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        newScheduleLabel.setText("Next Week :");
        newScheduleLabelPanel.add(newScheduleLabel, java.awt.BorderLayout.CENTER);

        newScheduleWrapperPanel.add(newScheduleLabelPanel);

        oldScheduleBorderPanel.add(newScheduleWrapperPanel, java.awt.BorderLayout.PAGE_END);
        newScheduleWrapperPanel.getAccessibleContext().setAccessibleName("newScheduleTable");

        oldScheduleLabelPanel.add(oldScheduleBorderPanel, java.awt.BorderLayout.PAGE_END);

        scheduleWrapperPanel.add(oldScheduleLabelPanel);

        newSchedulePanel.setPreferredSize(new java.awt.Dimension(453, 124));

        newScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        newScheduleTable.setName("newScheduleTable"); // NOI18N
        newScheduleTable.setShowGrid(true);
        newScheduleTable.getTableHeader().setReorderingAllowed(false);
        newSchedulePanel.setViewportView(newScheduleTable);

        scheduleWrapperPanel.add(newSchedulePanel);

        titlePanel.add(scheduleWrapperPanel, java.awt.BorderLayout.CENTER);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(backButton);

        testButton.setText("Test");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(testButton);

        jButton1.setText("Generate PDF");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        buttonPanel.add(jButton1);

        titlePanel.add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        FileUtils.writeDataToFile("next_week_schedule", nextWeekSchedule);

        // --- Logic to save the date using a list ---
        LocalDate today = LocalDate.now();
        LocalDate nextWeekMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // 1. Create a new list to hold just the date string
        DoublyLinkedList<String> dateList = new DoublyLinkedList<>();

        // 2. Add the date (as a String) to the list
        dateList.insertLast(nextWeekMonday.toString());

        // 3. Save the list containing the single date string
        FileUtils.writeDataToFile("schedule_info", dateList);

        javax.swing.JOptionPane.showMessageDialog(this, "Next week's schedule has been saved successfully!");
    }//GEN-LAST:event_saveButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.showPanel("doctorManagement");
    }//GEN-LAST:event_backButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        // Create a fake date that is 1 week in the future
        LocalDate futureDate = LocalDate.now().plusWeeks(1);
        System.out.println("--- TESTING ROLLOVER WITH FAKE DATE: " + futureDate + " ---");

        runScheduleRolloverCheck(futureDate);

        // 2. Re-load the data from the updated files into in-memory lists
        loadInitialData();

//        // 3. Re-populate both tables to show the new data on the screen
        populateTable(oldScheduleTable, thisWeekSchedule);
        populateTable(newScheduleTable, nextWeekSchedule);

        javax.swing.JOptionPane.showMessageDialog(this, "Rollover test complete and tables refreshed!");
    }//GEN-LAST:event_testButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Generate a PDF for the "Next Week" schedule 
        ReportGenerator.generateWeeklyScheduleReport(nextWeekSchedule, "Next Week's Duty Schedule", DAYS, SHIFTS);

        JOptionPane.showMessageDialog(this, "Schedule PDF has been generated!");
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel newScheduleLabel;
    private javax.swing.JPanel newScheduleLabelPanel;
    private javax.swing.JScrollPane newSchedulePanel;
    private javax.swing.JTable newScheduleTable;
    private javax.swing.JPanel newScheduleWrapperPanel;
    private javax.swing.JPanel oldScheduleBorderPanel;
    private javax.swing.JLabel oldScheduleLabel;
    private javax.swing.JPanel oldScheduleLabelPanel;
    private javax.swing.JScrollPane oldSchedulePanel;
    private javax.swing.JTable oldScheduleTable;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel scheduleWrapperPanel;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
