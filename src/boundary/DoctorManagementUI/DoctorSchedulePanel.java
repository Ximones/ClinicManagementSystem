package boundary.DoctorManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import control.DoctorManagementController.DoctorScheduleControl;
import enitity.Doctor;
import enitity.DutySlot;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import utility.ImageUtils;

/**
 *
 * @author Chok Chun Fai
 */
public class DoctorSchedulePanel extends javax.swing.JPanel {

    private final MainFrame mainFrame;
    private final DoctorScheduleControl control;

    /**
     * Creates new form DoctorSchedulePanel
     *
     * @param mainFrame
     */
    public DoctorSchedulePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

        this.control = new DoctorScheduleControl(this);

        // Setup UI
        setupTable(oldScheduleTable, false);
        setupTable(newScheduleTable, true);
        setupDoctorEditor();
        addTableUpdateListener();

        // Populate with data from controller
        refreshTables();
    }

    /**
     * Reloads data from the controller and repopulates both tables.
     */
    public void refreshTables() {
        populateTable(oldScheduleTable, control.getThisWeekSchedule());
        populateTable(newScheduleTable, control.getNextWeekSchedule());
    }

    private void addTableUpdateListener() {
        newScheduleTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column < 1) {
                    return; // Ignore updates to the "Days" column
                }
                String selectedDoctorName = (String) newScheduleTable.getValueAt(row, column);
                String day = (String) newScheduleTable.getValueAt(row, 0);
                String shift = (column == 1) ? control.SHIFTS.getFirst().getEntry() : control.SHIFTS.getLast().getEntry();

                // Delegate the update logic to the controller
                control.assignDoctorToSlot(day, shift, selectedDoctorName);
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
                return isEditable && column > 0;
            }
        };
        table.setModel(model);
        model.addColumn("Days / Shifts");
        model.addColumn(control.SHIFTS.getFirst().getEntry());
        model.addColumn(control.SHIFTS.getLast().getEntry());
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
        for (String day : control.DAYS) {
            model.addRow(new Object[model.getColumnCount()]);
            model.setValueAt(day, rowIndex, 0);

            Doctor morningDoctor = findDoctorForSlot(schedule, day, control.SHIFTS.getFirst().getEntry());
            Doctor eveningDoctor = findDoctorForSlot(schedule, day, control.SHIFTS.getLast().getEntry());

            String morningText = (morningDoctor != null) ? morningDoctor.getName() : "Unassigned";
            String eveningText = (eveningDoctor != null) ? eveningDoctor.getName() : "Unassigned";

            model.setValueAt(morningText, rowIndex, 1);
            model.setValueAt(eveningText, rowIndex, 2);
            rowIndex++;
        }
    }

    private void setupDoctorEditor() {
        JComboBox<String> doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem("Unassigned");

        DoublyLinkedList<Pair<String, Doctor>> masterDoctorList = control.getMasterDoctorList();
        if (masterDoctorList != null) {
            for (Pair<String, Doctor> pair : masterDoctorList) {
                if (!pair.getValue().getStatus().equals("Absent") && !pair.getValue().getStatus().equals("Resigned")) {
                    doctorComboBox.addItem(pair.getValue().getName());
                }
            }
        }

        DefaultCellEditor doctorEditor = new DefaultCellEditor(doctorComboBox);
        TableColumnModel columnModel = newScheduleTable.getColumnModel();
        columnModel.getColumn(1).setCellEditor(doctorEditor);
        columnModel.getColumn(2).setCellEditor(doctorEditor);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        scheduleWrapeprPanel = new javax.swing.JPanel();
        schedulePanel = new javax.swing.JPanel();
        oldScheduleLabelPanel = new javax.swing.JPanel();
        oldScheduleLabel = new javax.swing.JLabel();
        oldSchedulePanel = new javax.swing.JScrollPane();
        oldScheduleTable = new javax.swing.JTable();
        newScheduleLabelPanel = new javax.swing.JPanel();
        newScheduleLabel = new javax.swing.JLabel();
        newSchedulePanel = new javax.swing.JScrollPane();
        newScheduleTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Doctor Schedule");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        schedulePanel.setLayout(new java.awt.BorderLayout());

        oldScheduleLabelPanel.setLayout(new java.awt.BorderLayout());

        oldScheduleLabel.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        oldScheduleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        oldScheduleLabel.setText("This Week : ");
        oldScheduleLabelPanel.add(oldScheduleLabel, java.awt.BorderLayout.PAGE_START);

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

        oldScheduleLabelPanel.add(oldSchedulePanel, java.awt.BorderLayout.CENTER);

        schedulePanel.add(oldScheduleLabelPanel, java.awt.BorderLayout.PAGE_START);

        newScheduleLabelPanel.setLayout(new java.awt.BorderLayout());

        newScheduleLabel.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        newScheduleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newScheduleLabel.setText("Next Week :");
        newScheduleLabelPanel.add(newScheduleLabel, java.awt.BorderLayout.PAGE_START);

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

        newScheduleLabelPanel.add(newSchedulePanel, java.awt.BorderLayout.CENTER);

        schedulePanel.add(newScheduleLabelPanel, java.awt.BorderLayout.CENTER);

        scheduleWrapeprPanel.add(schedulePanel);

        titlePanel.add(scheduleWrapeprPanel, java.awt.BorderLayout.CENTER);

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
        control.saveSchedule();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.showPanel("doctorManagement");
    }//GEN-LAST:event_backButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        control.testRollover();
    }//GEN-LAST:event_testButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        control.generateScheduleReport();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel newScheduleLabel;
    private javax.swing.JPanel newScheduleLabelPanel;
    private javax.swing.JScrollPane newSchedulePanel;
    private javax.swing.JTable newScheduleTable;
    private javax.swing.JLabel oldScheduleLabel;
    private javax.swing.JPanel oldScheduleLabelPanel;
    private javax.swing.JScrollPane oldSchedulePanel;
    private javax.swing.JTable oldScheduleTable;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel schedulePanel;
    private javax.swing.JPanel scheduleWrapeprPanel;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
