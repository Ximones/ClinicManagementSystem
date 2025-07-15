/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.DoctorManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.Doctor;
import enitity.Schedule;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import utility.FileUtils;
import utility.ImageUtils;

/**
 *
 * @author deadb
 */
public class DoctorSchedulePanel extends javax.swing.JPanel {

    DoublyLinkedList weeklyDutySchedule = new DoublyLinkedList<>();

    // To create the schedule for the week:
    final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    final String[] SHIFTS = {"Morning (8am - 2pm)", "Evening (2pm - 8pm)"};

    /**
     * Creates new form DoctorSchedulePanel
     *
     * @param mainFrame
     */
    public DoctorSchedulePanel(MainFrame mainFrame) {
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

        for (String day : DAYS) {
            for (String shift : SHIFTS) {
                weeklyDutySchedule.insertLast(new Schedule(day, shift));
            }
        }

        populateDutyTable(weeklyDutySchedule);

        DoublyLinkedList<String> doctorNames = new DoublyLinkedList<>();
        DoublyLinkedList<Pair<String, Doctor>> doctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        doctorNames.insertLast("Unassigned"); // Add a default option
        
        // Skip Absent Doctor and Resigned Doctor 
        for (Pair<String, Doctor> pair : doctorList) {
            if (pair.getValue().getStatus().equals("Absent") || pair.getValue().getStatus().equals("Resigned")) {
                continue;
            }
            doctorNames.insertLast(pair.getValue().getName());
        }
        
        Object[] doctorNamesArray = doctorNames.toArray();

        // 1. Create the JComboBox with all the doctor names
        JComboBox<Object> jComboBox = new JComboBox<>(doctorNamesArray);

// 2. Create a cell editor using the JComboBox
        DefaultCellEditor doctorEditor = new DefaultCellEditor(jComboBox);

// 3. Apply this editor to each shift column in your table
// Assumes column 0 is "Day", and 1, 2, 3 are the shifts
        TableColumnModel columnModel = jTable1.getColumnModel();
        columnModel.getColumn(1).setCellEditor(doctorEditor); // Morning shift
        columnModel.getColumn(2).setCellEditor(doctorEditor); // Evening shift

        // Also in your constructor, after the code above
        jTable1.getModel().addTableModelListener((TableModelEvent e) -> {
            // Check if the event was a cell update
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Get the new value (the selected doctor's name)
                String selectedDoctorName = (String) jTable1.getValueAt(row, column);

                // Find the corresponding DutySlot and update it
                String day = (String) jTable1.getValueAt(row, 0); // Get day from first column
                String shift = jTable1.getColumnName(column);

//                    DutySlot targetSlot = findDutySlot(day, shift); // You need a helper method for this
//                    Doctor doctorToAssign = findDoctorByName(selectedDoctorName); // And this
//
//                    if (targetSlot != null) {
//                        targetSlot.assignDoctor(doctorToAssign); // Assigns the Doctor object or null
//                    }
            }
        });
    }

    private void populateDutyTable(DoublyLinkedList<Schedule> schedule) {
        // Get the table model
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setColumnIdentifiers(new Object[]{"Day / Shifts ", SHIFTS[0], SHIFTS[1]});

        model.setRowCount(0); // Clear existing data

        // Create one row for each day
        for (String day : DAYS) {
            Object[] rowData = new Object[4]; // 1 for the day + 3 for the shifts
            rowData[0] = day;

            // Find the doctors for each shift on that day
//            for (int i = 0; i < SHIFTS.length; i++) {
//                String shift = SHIFTS[i];
//                Doctor assignedDoctor = findDoctorForSlot(schedule, day, shift);
//                rowData[i + 1] = (assignedDoctor != null) ? assignedDoctor.getName() : "Unassigned";
//            }
            model.addRow(rowData);
        }

    }

    // A helper method to find the right doctor in your list
    private Doctor findDoctorForSlot(DoublyLinkedList<Schedule> schedule, String day, String shift) {
        for (Schedule slot : schedule) {
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

        jComboBox1 = new javax.swing.JComboBox<>();
        logoPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

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

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("This Week : ");
        jPanel3.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 124));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Monday", null, null},
                {"Tuesday", null, null},
                {"Wednesday", null, null},
                {"Thursday", null, null},
                {"Friday", null, null},
                {"Saturday", null, null}
            },
            new String [] {
                "Days & Shifts", "Morning ( 8am - 2pm )", "Evening ( 2pm - 8pm )"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setShowGrid(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setPreferredSize(new java.awt.Dimension(100, 23));

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Corbel", 1, 18)); // NOI18N
        jLabel2.setText("Next Week :");
        jPanel5.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jPanel3.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanel1.add(jPanel3);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(453, 124));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Monday", null, null},
                {"Tuesday", null, null},
                {"Wednesday", null, null},
                {"Thursday", null, null},
                {"Friday", null, null},
                {"Saturday", null, null}
            },
            new String [] {
                "Days & Shifts", "Morning ( 8am - 2pm )", "Evening ( 2pm - 8pm )"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable2.setShowGrid(true);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel1.add(jScrollPane2);

        titlePanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        jButton1.setText("jButton1");
        buttonPanel.add(jButton1);

        jButton2.setText("jButton2");
        buttonPanel.add(jButton2);

        titlePanel.add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
