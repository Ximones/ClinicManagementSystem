/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.PatientManagementUI;

import utility.ImageUtils;
import boundary.MainFrame;
import enitity.Patient;
import adt.DoublyLinkedList;
import control.PatientController.PatientControl;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import utility.ReportGenerator;

/**
 *
 * @author szepi
 */
public class PatientRegistrationPanel extends javax.swing.JPanel {
    private MainFrame mainFrame;
    private PatientControl patientControl;   
    private DefaultTableModel tableModel;

    public PatientRegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.patientControl = new PatientControl(); 

        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        loadAndDisplayPatients();

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterPatients(); }
            @Override public void removeUpdate(DocumentEvent e) { filterPatients(); }
            @Override public void changedUpdate(DocumentEvent e) { filterPatients(); }
        });

        AddPatient.addActionListener(e -> addPatient());
        Done.addActionListener(e -> exitWithoutSaving());
    }

    // Refresh from controller
    public void reloadData() {
        loadAndDisplayPatients();
    }

    private void setupTable() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Patient ID", "Name", "Age", "IC", "Gender", "Contact", "Email", "Address", "Date of Reg"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 2) ? Integer.class : String.class;
            }
        };
        tableModel = model;
        patientTable.setModel(tableModel);
        patientTable.setAutoCreateRowSorter(true);
    }

    private void loadAndDisplayPatients() {
        tableModel.setRowCount(0);
        DoublyLinkedList<Patient> list = patientControl.getAllPatients();
        if (list != null) {
            for (Patient p : list) {
                tableModel.addRow(new Object[]{
                    p.getPatientID(), p.getPatientName(), p.getPatientAge(),
                    p.getPatientIC(), p.getGender(), p.getContact(),
                    p.getEmail(), p.getAddress(), p.getDateOfRegistration()
                });
            }
        }
    }

        private void addPatient() {
        
             PatientDialog dialog = new PatientDialog(mainFrame, true, patientControl);
             dialog.setVisible(true);

             Patient newPatient = dialog.getResultPatient();
             if (newPatient != null) {
              patientControl.addPatient(newPatient);   
              loadAndDisplayPatients();
              JOptionPane.showMessageDialog(this,
            "Patient " + newPatient.getPatientName() +
            " (ID: " + newPatient.getPatientID() + ") added successfully!");
    } else {
        JOptionPane.showMessageDialog(this, "Patient registration cancelled.");
    }
}

private void filterPatients() {
    String selectedFilter = (String) filterBox.getSelectedItem();
    String keyword = filterField.getText().trim(); // do not lowercase for exact match
    tableModel.setRowCount(0);

    for (Patient p : patientControl.getAllPatients()) {
        String valueToCheck = "";
        switch (selectedFilter) {
            case "Patient ID": valueToCheck = p.getPatientID(); break;
            case "Name": valueToCheck = p.getPatientName(); break;
            case "Age": valueToCheck = String.valueOf(p.getPatientAge()); break;
            case "IC": valueToCheck = p.getPatientIC(); break;
            case "Gender": valueToCheck = p.getGender(); break;
            case "Date of Reg": valueToCheck = p.getDateOfRegistration(); break;
        }

        if (valueToCheck != null) {
            boolean matches;
            if (selectedFilter.equals("Gender")) {
                // Exact match for gender
                matches = valueToCheck.equals(keyword); 
            } else {
                // Case-insensitive partial match for other fields
                matches = valueToCheck.toLowerCase().contains(keyword.toLowerCase());
            }

            if (matches) {
                tableModel.addRow(new Object[]{
                    p.getPatientID(), p.getPatientName(), p.getPatientAge(),
                    p.getPatientIC(), p.getGender(), p.getContact(),
                    p.getEmail(), p.getAddress(), p.getDateOfRegistration()
                });
            }
        }
    }
}


    private void exitWithoutSaving() {
    if (patientTable.isEditing()) {
        patientTable.getCellEditor().stopCellEditing();
    }
    JOptionPane.showMessageDialog(this, "Exited without saving changes.");
    mainFrame.showPanel("patientManagement");
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
        sortLabel = new javax.swing.JLabel();
        sortBox = new javax.swing.JComboBox<>();
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

        sortLabel.setText("Sort ID by:");
        searchPanel.add(sortLabel);

        sortBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ASC", "DESC"
            + ""}));
sortBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortBoxActionPerformed(evt);
    }
    });
    searchPanel.add(sortBox);

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

    if (patientTable.isEditing()) {
        patientTable.getCellEditor().stopCellEditing();
    }
    mainFrame.showPanel("patientManagement");
    }//GEN-LAST:event_DoneActionPerformed

    private void GenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateReportActionPerformed
        try {
            ReportGenerator.generatePatientAgeRangeReport(patientControl.getAllPatients());
            JOptionPane.showMessageDialog(this, "Patient Age Range Report generated successfully!");
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
        String patientId = tableModel.getValueAt(selectedRow, 0).toString();
        Patient selectedPatient = patientControl.searchPatientById(patientId);
        if (selectedPatient == null) {
            JOptionPane.showMessageDialog(this, "Patient not found.");
            return;
        }
        PatientDialog dialog = new PatientDialog(mainFrame, true, selectedPatient);
        dialog.setVisible(true);
        Patient updatedPatient = dialog.getResultPatient();
        if (updatedPatient != null) {
            patientControl.updatePatient(updatedPatient);  // ✅ controller updates
            loadAndDisplayPatients();
            JOptionPane.showMessageDialog(this, "Patient updated successfully!");
        }
    }//GEN-LAST:event_EditPatientActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    private void sortBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBoxActionPerformed
         String selectedSort = (String) sortBox.getSelectedItem();
        boolean asc = "ASC".equalsIgnoreCase(selectedSort);
        patientControl.sortById(asc);   // ✅ delegate sorting
        loadAndDisplayPatients();
    }//GEN-LAST:event_sortBoxActionPerformed

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
    private javax.swing.JComboBox<String> sortBox;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
