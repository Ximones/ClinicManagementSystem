/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.DoctorManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.Doctor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import utility.ImageUtils;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import utility.FileUtils;

/**
 *
 * @author Chok Chun Fai
 */
public class DoctorInformationPanel extends javax.swing.JPanel {

    MainFrame mainFrame;
    private DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;

    /**
     * Creates new form DoctorInformationPanel
     *
     * @param mainFrame
     */
    public DoctorInformationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();

        String[] position = {"Consultant", "Doctor", "Internship"};
        String[] status = {"Present", "Absent", "Resigned"};

        JComboBox<String> positionComboBox = new JComboBox<>(position);
        JComboBox<String> statusComboBox = new JComboBox<>(status);

        // Create a cell editor 
        DefaultCellEditor positionEditor = new DefaultCellEditor(positionComboBox);
        DefaultCellEditor statusEditor = new DefaultCellEditor(statusComboBox);

        // Get the column model from table
        TableColumnModel columnModel = doctorTable.getColumnModel();

        // Change the index to match table's column order
        TableColumn positionColumn = columnModel.getColumn(4);
        TableColumn statusColumn = columnModel.getColumn(5);

        // Set the custom editor for that column
        positionColumn.setCellEditor(positionEditor);
        statusColumn.setCellEditor(statusEditor);

        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

//        DoublyLinkedList<Pair<String, Doctor>> doctorList = new DoublyLinkedList<>();
//        Doctor doc1 = new Doctor("Simon", 20, "01118566866", "Doctor", "Present");
//        Doctor doc2 = new Doctor("ZB", 21, "01118566866", "Doctor", "Absent");
//        Doctor doc3 = new Doctor("JY", 30, "01118566866", "Consultant", "Resigned");
//        Doctor doc4 = new Doctor("Desmond", 32, "01118566866", "Internship", "Present");
//
//        Pair<String, Doctor> doctorPair1 = new Pair<>(doc1.getDoctorID(), doc1);
//        Pair<String, Doctor> doctorPair2 = new Pair<>(doc2.getDoctorID(), doc2);
//        Pair<String, Doctor> doctorPair3 = new Pair<>(doc3.getDoctorID(), doc3);
//        Pair<String, Doctor> doctorPair4 = new Pair<>(doc4.getDoctorID(), doc4);
//
//        doctorList.insertFirst(doctorPair1);
//        doctorList.insertLast(doctorPair2);
//        doctorList.insertLast(doctorPair3);
//        doctorList.insertLast(doctorPair4);
//        masterDoctorList = doctorList;
//        populateDoctorTable(masterDoctorList);
        loadInitialData();
        populateDoctorTable(masterDoctorList);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Called when a character is inserted
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Called when a character is removed
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not typically used for plain text fields, but good to include
                filterTable();
            }
        });

        doctorTable.getModel().addTableModelListener((e) -> {
            // Make sure the event is an actual cell update
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                // Get the row and column that were edited
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Get the Doctor ID from the first column of the edited row
                String doctorId = (String) doctorTable.getValueAt(row, 0);

                // Find the corresponding doctor in the master list
                Pair<String, Doctor> targetPair = findDoctorPairById(doctorId);

                if (targetPair != null) {
                    Doctor doctorToUpdate = targetPair.getValue();
                    Object newValue = doctorTable.getValueAt(row, column);

                    // Check which column was edited and update the object
                    if (column == 4) { // Column 4 is "Position"
                        doctorToUpdate.setPosition((String) newValue);
                        System.out.println("Updated " + doctorToUpdate.getName() + "'s position to " + newValue);
                    } else if (column == 5) { // Column 5 is "Status"
                        doctorToUpdate.setStatus((String) newValue);
                        System.out.println("Updated " + doctorToUpdate.getName() + "'s status to " + newValue);
                    }
                }
            }
        });
    }

    private Pair<String, Doctor> findDoctorPairById(String id) {
        if (id == null || masterDoctorList == null) {
            return null;
        }
        for (Pair<String, Doctor> pair : masterDoctorList) {
            if (id.equals(pair.getKey())) {
                return pair;
            }
        }
        return null;
    }

    private void loadInitialData() {
        DoublyLinkedList<Pair<String, Doctor>> doctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");

        if (!doctorList.isEmpty()) {
            Doctor.setDoctorIndex(doctorList.getSize());
        }

        masterDoctorList = doctorList;
    }

    /**
     * Clears the table and populates it with a list of doctors.
     *
     * @param doctorList The list of Doctor objects to display.
     */
    private void populateDoctorTable(DoublyLinkedList<Pair<String, Doctor>> doctorMasterList) {
        // 1. Get the Table Model
        DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();

        // 2. Clear Previous Data
        // This removes all existing rows from the table.
        model.setRowCount(0);

        for (Pair<String, Doctor> pair : doctorMasterList) {

            // Get the Doctor object from the Pair
            Doctor doctor = pair.getValue();

            // Create an array of objects for each row
            Object[] row = {
                pair.getKey(), // The Doctor's ID from the Pair's key
                doctor.getName(), // The Doctor's name from the Doctor object
                doctor.getAge(), // Example: add other fields as needed
                doctor.getPhoneNumber(),
                doctor.getPosition(),
                doctor.getStatus()
            };

            // Add the row to the model, which updates the JTable
            model.addRow(row);
        }
    }

    private void filterTable() {
        // Get the current search inputs
        String selectedCriterion = (String) filterBox.getSelectedItem();
        String searchText = filterField.getText().trim().toLowerCase();

        // If search text is empty, show all doctors
        if (searchText.isEmpty()) {
            populateDoctorTable(masterDoctorList);
            return;
        }

        DoublyLinkedList<Pair<String, Doctor>> searchResults = new DoublyLinkedList<>();

        // Loop through the master list to find matches
        for (Pair<String, Doctor> pair : masterDoctorList) {
            Doctor doctor = pair.getValue();
            String doctorId = pair.getKey();
            boolean match = false;

            if ("ID".equals(selectedCriterion)) {
                if (doctorId.toLowerCase().equals(searchText)) {
                    match = true;
                }
            } else if ("Name".equals(selectedCriterion)) {
                if (doctor.getName().toLowerCase().contains(searchText)) {
                    match = true;
                }
            } else if ("Position".equals(selectedCriterion)) {
                if (doctor.getPosition().toLowerCase().contains(searchText)) {
                    match = true;
                }
            }

            if (match) {
                searchResults.insertLast(pair);
            }
        }

        // Update the table with the filtered results
        populateDoctorTable(searchResults);
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
        searchWrapperPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        doctorTablePanel = new javax.swing.JScrollPane();
        doctorTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        addDoctorButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Doctor Information");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        filterLabel.setText("Filter By :");
        searchPanel.add(filterLabel);

        filterBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Name", "Position" }));
        filterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBoxActionPerformed(evt);
            }
        });
        searchPanel.add(filterBox);

        filterField.setColumns(15);
        filterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterFieldActionPerformed(evt);
            }
        });
        searchPanel.add(filterField);

        searchWrapperPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        doctorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Age", "Contact", "Position", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        doctorTable.getTableHeader().setReorderingAllowed(false);
        doctorTablePanel.setViewportView(doctorTable);

        searchWrapperPanel.add(doctorTablePanel, java.awt.BorderLayout.CENTER);

        addDoctorButton.setText("Add Doctor");
        addDoctorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDoctorButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(addDoctorButton);

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(doneButton);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addDoctorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDoctorButtonActionPerformed
        DoctorDialog dialog = new DoctorDialog(mainFrame, true);

        dialog.setVisible(true);

        // Get the result from the dialog
        Pair<String, Doctor> newDoctorPair = dialog.getResult();

        // Check if the user clicked "Save" (result will not be null)
        if (newDoctorPair != null) {
            // Add the new doctor to your master list
            masterDoctorList.insertLast(newDoctorPair);

            // Refresh the table with the complete, updated list
            populateDoctorTable(masterDoctorList);
        }
    }//GEN-LAST:event_addDoctorButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed

        FileUtils.writeDataToFile("doctors", masterDoctorList);
        mainFrame.showPanel("doctorManagement");
    }//GEN-LAST:event_doneButtonActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton addDoctorButton;
    private javax.swing.JTable doctorTable;
    private javax.swing.JScrollPane doctorTablePanel;
    private javax.swing.JButton doneButton;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
