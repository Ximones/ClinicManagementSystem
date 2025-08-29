/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.PatientManagementUI;

import utility.ImageUtils;
import boundary.MainFrame;
import enitity.Patient;
import adt.DoublyLinkedList;
import adt.Pair; // Assuming Pair is in adt package
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import utility.FileUtils;
import utility.ReportGenerator;

/**
 *
 * @author szepi
 */
public class PatientRegistrationPanel extends javax.swing.JPanel {

     private MainFrame mainFrame;
     private DoublyLinkedList<Patient> patientList = new DoublyLinkedList<>();
     private DefaultTableModel tableModel;
     
     
    /**
     * Creates new form PatientRegistrationPanel
     */
    public PatientRegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
 
        
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        loadAndDisplayPatients();
        
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Called when a character is inserted
                filterPatients();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Called when a character is removed
                filterPatients();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not typically used for plain text fields, but good to include
                filterPatients();
            }
        });
        AddPatient.addActionListener(e -> addPatient());
        Done.addActionListener(e -> {
        savePatientData();
        JOptionPane.showMessageDialog(this, "Patient data saved.");
    });

            
    }
    
    // Public method to re-read from disk and refresh table when panel is shown
    public void reloadData() {
        loadAndDisplayPatients();
    }

    private Patient findPatientById(String id) {
    for (Patient p : patientList) {
        if (id.equals(p.getPatientID())) {
            return p;
        }
    }
    return null;
}
    
private void setupTable() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Patient ID", "Name", "Age", "IC", "Gender", "Contact", "Email", "Address", "Date of Reg"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == 2) ? Integer.class : String.class; // Age as Integer
        }
    };

    tableModel = model;
    patientTable.setModel(tableModel);
    patientTable.setAutoCreateRowSorter(true); // optional but handy

    // Push table edits back into the underlying list
    tableModel.addTableModelListener(e -> {
        if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;

        int row = e.getFirstRow();
        int col = e.getColumn();
        if (row < 0 || col < 0) return;

        String id = tableModel.getValueAt(row, 0).toString(); // stable key
        Patient p = findPatientById(id);
        if (p == null) return;

        Object newValue = tableModel.getValueAt(row, col);

        try {
            switch (col) {
                case 1: p.setPatientName(newValue.toString()); break;
                case 2: p.setPatientAge(Integer.parseInt(newValue.toString())); break;
                case 3: p.setPatientIC(newValue.toString()); break;
                case 4: p.setGender(newValue.toString()); break;
                case 5: p.setContact(newValue.toString()); break;
                case 6: p.setEmail(newValue.toString()); break;
                case 7: p.setAddress(newValue.toString()); break;
                case 8: p.setDateOfRegistration(newValue.toString()); break;
            }
            savePatientData(); // persist after each edit (or only on Done)
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number for Age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            // reload the table row from the object to revert the bad edit
            reloadPatientRow(row, p);
        }
    });
}



// Helper: refresh one row from a Patient object
private void reloadPatientRow(int row, Patient p) {
    tableModel.setValueAt(p.getPatientID(), row, 0);
    tableModel.setValueAt(p.getPatientName(), row, 1);
    tableModel.setValueAt(p.getPatientAge(), row, 2);
    tableModel.setValueAt(p.getPatientIC(), row, 3);
    tableModel.setValueAt(p.getGender(), row, 4);
    tableModel.setValueAt(p.getContact(), row, 5);
    tableModel.setValueAt(p.getEmail(), row, 6);
    tableModel.setValueAt(p.getAddress(), row, 7);
    tableModel.setValueAt(p.getDateOfRegistration(), row, 8);
}

    
    
private void loadAndDisplayPatients() {
    int maxId = 0;
    tableModel.setRowCount(0);

    // Initialize as Patient list, not Pair
    patientList = new DoublyLinkedList<>();

    // Load data
    DoublyLinkedList<Patient> loadedList =
        (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");

    if (loadedList != null) {
        patientList = loadedList;

        // Loop through patients directly
        for (Patient p : patientList) {
            tableModel.addRow(new Object[]{
                p.getPatientID(),
                p.getPatientName(),
                p.getPatientAge(),
                p.getPatientIC(),
                p.getGender(),
                p.getContact(),
                p.getEmail(),
                p.getAddress(),
                p.getDateOfRegistration()
            });

            // Track max patient ID number
            try {
                String idNumStr = p.getPatientID().substring(1); // assumes format P###
                int currentIdNum = Integer.parseInt(idNumStr);
                if (currentIdNum > maxId) maxId = currentIdNum;
            } catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
                System.err.println("Warning: Could not parse patient ID: " + p.getPatientID());
            }
        }

        // Update static patient index
        Patient.setPatientIndex(maxId);
    } else {
        System.out.println("No patient data found or unable to read file. Starting with empty list.");
    }
}
     
    private void savePatientData() {
    utility.FileUtils.writeDataToFile("patients", patientList);
}

    
    private void addPatient() {
    PatientDialog dialog = new PatientDialog(mainFrame, true);
    dialog.setVisible(true);

    Patient newPatient = dialog.getResultPatient(); // ✅ should return a Patient, not Pair
    if (newPatient != null) {
        patientList.insertLast(newPatient);
        tableModel.addRow(new Object[]{
            newPatient.getPatientID(),
            newPatient.getPatientName(),
            newPatient.getPatientAge(),
            newPatient.getPatientIC(),
            newPatient.getGender(),
            newPatient.getContact(),
            newPatient.getEmail(),
            newPatient.getAddress(),
            newPatient.getDateOfRegistration()
        });
        savePatientData();
        JOptionPane.showMessageDialog(this,
            "Patient " + newPatient.getPatientName() +
            " (ID: " + newPatient.getPatientID() + ") added successfully!");
    } else {
        JOptionPane.showMessageDialog(this, "Patient registration cancelled.");
    }
}

    
private void filterPatients() {
    String selectedFilter = (String) filterBox.getSelectedItem();
    String keyword = filterField.getText().trim().toLowerCase();

    tableModel.setRowCount(0);

    for (Patient p : patientList) {
        String valueToCheck = "";
        switch (selectedFilter) {
            case "Patient ID": valueToCheck = p.getPatientID(); break;
            case "Name": valueToCheck = p.getPatientName(); break;
            case "Age": valueToCheck = String.valueOf(p.getPatientAge()); break;
            case "IC": valueToCheck = p.getPatientIC(); break;
            case "Gender": valueToCheck = p.getGender(); break;
            case "Date of Reg": valueToCheck = p.getDateOfRegistration(); break;
        }

        // Normalize both sides
        if (valueToCheck != null && valueToCheck.trim().toLowerCase().contains(keyword)) {
            tableModel.addRow(new Object[]{
                p.getPatientID(),
                p.getPatientName(),
                p.getPatientAge(),
                p.getPatientIC(),
                p.getGender(),
                p.getContact(),
                p.getEmail(),
                p.getAddress(),
                p.getDateOfRegistration()
            });
        }
    }
}


    
    
    
    

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        searchWrapperPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        patientTablePanel = new javax.swing.JScrollPane();
        patientTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        EditPatient = new javax.swing.JButton();
        AddPatient = new javax.swing.JButton();
        GenerateReport = new javax.swing.JButton();
        Done = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Patient Registration ");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        filterLabel.setText("Filter By :");
        searchPanel.add(filterLabel);

        filterBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Patient ID", "Name", "Age", "IC", "Gender", "Date of Reg" }));
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

        patientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        patientTable.getTableHeader().setReorderingAllowed(false);
        patientTablePanel.setViewportView(patientTable);

        searchWrapperPanel.add(patientTablePanel, java.awt.BorderLayout.CENTER);

        EditPatient.setText("Edit Patient");
        EditPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPatientActionPerformed(evt);
            }
        });
        ButtonPanel.add(EditPatient);

        AddPatient.setText("Add Patient");
        AddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPatientActionPerformed(evt);
            }
        });
        ButtonPanel.add(AddPatient);

        GenerateReport.setText("Generate Report");
        GenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateReportActionPerformed(evt);
            }
        });
        ButtonPanel.add(GenerateReport);

        Done.setText("Done");
        Done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneActionPerformed(evt);
            }
        });
        ButtonPanel.add(Done);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed

    private void AddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPatientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddPatientActionPerformed

    private void DoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneActionPerformed
     // ✅ Make sure last cell edit is committed
    if (patientTable.isEditing()) {
        patientTable.getCellEditor().stopCellEditing();
    }

    // ✅ Sync all table rows back into patientList
    for (int row = 0; row < tableModel.getRowCount(); row++) {
        String id = tableModel.getValueAt(row, 0).toString();
        Patient p = findPatientById(id);
        if (p == null) continue;

        p.setPatientName(tableModel.getValueAt(row, 1).toString());
        p.setPatientAge(Integer.parseInt(tableModel.getValueAt(row, 2).toString()));
        p.setPatientIC(tableModel.getValueAt(row, 3).toString());
        p.setGender(tableModel.getValueAt(row, 4).toString());
        p.setContact(tableModel.getValueAt(row, 5).toString());
        p.setEmail(tableModel.getValueAt(row, 6).toString());
        p.setAddress(tableModel.getValueAt(row, 7).toString());
        p.setDateOfRegistration(tableModel.getValueAt(row, 8).toString());
    }

    // ✅ Save updated list once
    FileUtils.writeDataToFile("patients", patientList);
    JOptionPane.showMessageDialog(this, "All patient updates saved!");
    mainFrame.showPanel("patientManagement");

    }//GEN-LAST:event_DoneActionPerformed

    private void GenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateReportActionPerformed
          try {
        // Generate the Patient Age Range Report
        ReportGenerator.generatePatientAgeRangeReport(patientList);

        JOptionPane.showMessageDialog(this, 
            "Patient Age Range Report generated successfully!");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error generating report: " + ex.getMessage(), 
            "Report Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_GenerateReportActionPerformed

    private void EditPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditPatientActionPerformed
    int selectedRow = patientTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
        return;
    }

    // Get patient ID from selected row
    String patientId = tableModel.getValueAt(selectedRow, 0).toString();
    Patient selectedPatient = findPatientById(patientId);
    if (selectedPatient == null) {
        JOptionPane.showMessageDialog(this, "Patient not found.");
        return;
    }

    // Open the PatientDialog in "edit mode"
    PatientDialog dialog = new PatientDialog(mainFrame, true, selectedPatient);
    dialog.setVisible(true);
    Patient updatedPatient = dialog.getResultPatient();

    if (updatedPatient != null) {
        // Refresh row in table directly (since PatientDialog already updates the object)
        reloadPatientRow(selectedRow, selectedPatient);
        savePatientData();
        JOptionPane.showMessageDialog(this, "Patient updated successfully!");
    }
    }//GEN-LAST:event_EditPatientActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddPatient;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton Done;
    private javax.swing.JButton EditPatient;
    private javax.swing.JButton GenerateReport;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTable patientTable;
    private javax.swing.JScrollPane patientTablePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
