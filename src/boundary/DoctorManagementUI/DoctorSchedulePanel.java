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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import utility.FileUtils;
import utility.ImageUtils;

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
    final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    final String[] SHIFTS = {"Morning (8am - 2pm)", "Evening (2pm - 8pm)"};

    /**
     * Creates new form DoctorSchedulePanel
     *
     * @param mainFrame
     */
    public DoctorSchedulePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        runScheduleRolloverCheck(LocalDate.now());
        loadInitialData();

//        Initialize Data for this week
//        thisWeekSchedule = new DoublyLinkedList<>();
//        DutySlot dataSlot1 = new DutySlot("Monday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot2 = new DutySlot("Tuesday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot3 = new DutySlot("Wednesday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot4 = new DutySlot("Thursday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot5 = new DutySlot("Friday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot6 = new DutySlot("Saturday", "Morning (8am - 2pm)", masterDoctorList.getElement(1).getEntry().getValue());
//        DutySlot dataSlot7 = new DutySlot("Monday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//        DutySlot dataSlot8 = new DutySlot("Tuesday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//        DutySlot dataSlot9 = new DutySlot("Wednesday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//        DutySlot dataSlot10 = new DutySlot("Thursday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//        DutySlot dataSlot11 = new DutySlot("Friday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//        DutySlot dataSlot12 = new DutySlot("Saturday", "Evening (2pm - 8pm)", masterDoctorList.getElement(4).getEntry().getValue());
//
//        thisWeekSchedule.insertLast(dataSlot1);
//        thisWeekSchedule.insertLast(dataSlot2);
//        thisWeekSchedule.insertLast(dataSlot3);
//        thisWeekSchedule.insertLast(dataSlot4);
//        thisWeekSchedule.insertLast(dataSlot5);
//        thisWeekSchedule.insertLast(dataSlot6);
//        thisWeekSchedule.insertLast(dataSlot7);
//        thisWeekSchedule.insertLast(dataSlot8);
//        thisWeekSchedule.insertLast(dataSlot9);
//        thisWeekSchedule.insertLast(dataSlot10);
//        thisWeekSchedule.insertLast(dataSlot11);
//        thisWeekSchedule.insertLast(dataSlot12);
//        FileUtils.writeDataToFile("this_week_schedule", thisWeekSchedule);
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
        nextWeekSchedule.displayFromFirst(nextWeekSchedule.getFirst());
        if (nextWeekSchedule == null) {
            nextWeekSchedule = new DoublyLinkedList<>();
            for (String day : DAYS) {
                for (String shift : SHIFTS) {
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
        DoublyLinkedList<String> doctorNames = new DoublyLinkedList<>();
        doctorNames.insertLast("Unassigned");
        for (Pair<String, Doctor> pair : masterDoctorList) {
            if (!pair.getValue().getStatus().equals("Absent") && !pair.getValue().getStatus().equals("Resigned")) {
                doctorNames.insertLast(pair.getValue().getName());
            }
        }
        Object[] doctorNamesArray = doctorNames.toArray();
        JComboBox<Object> doctorComboBox = new JComboBox<>(doctorNamesArray);
        DefaultCellEditor doctorEditor = new DefaultCellEditor(doctorComboBox);

       
        TableColumnModel columnModel = newScheduleTable.getColumnModel();
        columnModel.getColumn(1).setCellEditor(doctorEditor);
        columnModel.getColumn(2).setCellEditor(doctorEditor);

        // --- 4. Add Listener to newScheduleTable to Handle Assignments ---
        newScheduleTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                String selectedDoctorName = (String) newScheduleTable.getValueAt(row, column);
                String day = (String) newScheduleTable.getValueAt(row, 0);
                String shift = newScheduleTable.getColumnName(column);

                // Find the slot in our 'nextWeekSchedule' data and update it
                DutySlot targetSlot = findDutySlot(nextWeekSchedule, day, shift);
                Doctor doctorToAssign = findDoctorByName(selectedDoctorName);

                if (targetSlot != null) {
                    targetSlot.setAssignedDoctor(doctorToAssign);
                    System.out.println("Assigned " + selectedDoctorName + " to " + day + " " + shift);
                }
            }
        });
    }

    private void populateTable(javax.swing.JTable table, DoublyLinkedList<DutySlot> schedule) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String day : DAYS) {
            Object[] rowData = new Object[3]; // 1 for day + 2 for shifts
            rowData[0] = day;

            // Find assigned doctors for each shift and populate the row
            Doctor morningDoctor = findDoctorForSlot(schedule, day, SHIFTS[0]);
            Doctor eveningDoctor = findDoctorForSlot(schedule, day, SHIFTS[1]);

            rowData[1] = (morningDoctor != null) ? morningDoctor.getName() : "Unassigned";
            rowData[2] = (eveningDoctor != null) ? eveningDoctor.getName() : "Unassigned";

            model.addRow(rowData);
        }
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

    // A helper method to find the right doctor in your list
    private Doctor findDoctorForSlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {
        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot.getAssignedDoctor();
            }
        }
        return null;
    }

    // Helper method to find a schedule slot
    private DutySlot findDutySlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {
        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot;
            }
        }
        return null;
    }

// Helper method to find a Doctor object by their name
    private Doctor findDoctorByName(String name) {
        if (name == null || name.equals("Unassigned")) {
            return null;
        }
        for (Pair<String, Doctor> pair : masterDoctorList) {
            if (pair.getValue().getName().equals(name)) {
                return pair.getValue();
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
                {"Monday", null, null},
                {"Tuesday", null, null},
                {"Wednesday", null, null},
                {"Thursday", null, null},
                {"Friday", null, null},
                {"Saturday", null, null}
            },
            new String [] {
                "Days & Shifts", "Morning (8am - 2pm)", "Evening (2pm - 8pm)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        oldScheduleTable.setShowGrid(true);
        oldScheduleTable.getTableHeader().setReorderingAllowed(false);
        oldSchedulePanel.setViewportView(oldScheduleTable);
        if (oldScheduleTable.getColumnModel().getColumnCount() > 0) {
            oldScheduleTable.getColumnModel().getColumn(0).setResizable(false);
            oldScheduleTable.getColumnModel().getColumn(1).setResizable(false);
            oldScheduleTable.getColumnModel().getColumn(2).setResizable(false);
        }

        oldScheduleBorderPanel.add(oldSchedulePanel, java.awt.BorderLayout.CENTER);

        newScheduleWrapperPanel.setPreferredSize(new java.awt.Dimension(100, 23));

        newScheduleLabelPanel.setLayout(new java.awt.BorderLayout());

        newScheduleLabel.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        newScheduleLabel.setText("Next Week :");
        newScheduleLabelPanel.add(newScheduleLabel, java.awt.BorderLayout.CENTER);

        newScheduleWrapperPanel.add(newScheduleLabelPanel);

        oldScheduleBorderPanel.add(newScheduleWrapperPanel, java.awt.BorderLayout.PAGE_END);

        oldScheduleLabelPanel.add(oldScheduleBorderPanel, java.awt.BorderLayout.PAGE_END);

        scheduleWrapperPanel.add(oldScheduleLabelPanel);

        newSchedulePanel.setPreferredSize(new java.awt.Dimension(453, 124));

        newScheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Monday", null, null},
                {"Tuesday", null, null},
                {"Wednesday", null, null},
                {"Thursday", null, null},
                {"Friday", null, null},
                {"Saturday", null, null}
            },
            new String [] {
                "Days & Shifts", "Morning (8am - 2pm)", "Evening (2pm - 8pm)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        newScheduleTable.setShowGrid(true);
        newScheduleTable.getTableHeader().setReorderingAllowed(false);
        newSchedulePanel.setViewportView(newScheduleTable);
        if (newScheduleTable.getColumnModel().getColumnCount() > 0) {
            newScheduleTable.getColumnModel().getColumn(0).setResizable(false);
            newScheduleTable.getColumnModel().getColumn(1).setResizable(false);
            newScheduleTable.getColumnModel().getColumn(2).setResizable(false);
        }

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

        jButton1.setText("Test");
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Create a fake date that is 1 week in the future
        LocalDate futureDate = LocalDate.now().plusWeeks(1);
        System.out.println("--- TESTING ROLLOVER WITH FAKE DATE: " + futureDate + " ---");

        runScheduleRolloverCheck(futureDate);

        // 2. Re-load the data from the updated files into in-memory lists
        loadInitialData();

        // 3. Re-populate both tables to show the new data on the screen
        populateTable(oldScheduleTable, thisWeekSchedule);
        populateTable(newScheduleTable, nextWeekSchedule);

        javax.swing.JOptionPane.showMessageDialog(this, "Rollover test complete and tables refreshed!");
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
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
