/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.MedicalTreatmentManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import control.TreatmentController.TreatmentControl;
import enitity.Treatment;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import utility.FileUtils;
import utility.ImageUtils;
import utility.ReportGenerator;

/**
 *
 * @author User
 */
public class TreatmentHistoryPanel extends javax.swing.JPanel {
    
    private MainFrame mainFrame;
    private TreatmentControl treatmentControl = new TreatmentControl();
    private DoublyLinkedList<Pair<String, Treatment>> masterTreatmentList;

    /**
     * Creates new form TreatmentHistoryPanel
     * @param mainFrame
     */
    public TreatmentHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        loadAndDisplayTreatments();
        
        // Add a listener to the search field for real-time filtering
        searchInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
    }
    
    // Sets up the columns for the JTable
    private void setupTable() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        historyTable.setModel(model);
        model.addColumn("Treatment ID");
        model.addColumn("Patient ID");
        model.addColumn("Patient Name");
        model.addColumn("Doctor ID");
        model.addColumn("Doctor Name");
        model.addColumn("Diagnosis");
        model.addColumn("Treatment Type");
        model.addColumn("Notes");
        model.addColumn("Date & Time");
    }

    // Loads data from the file and populates the table
    public void loadAndDisplayTreatments() {
        masterTreatmentList = (DoublyLinkedList<Pair<String, Treatment>>) FileUtils.readDataFromFile("treatments");
        if (masterTreatmentList == null) {
            masterTreatmentList = new DoublyLinkedList<>();
        }
        populateTable(masterTreatmentList);
    }
    
    // Public method for MainFrame to call to refresh data
    public void reloadData() {
        loadAndDisplayTreatments();
    }
    
    // Helper method to populate the table with a given list
    private void populateTable(DoublyLinkedList<Pair<String, Treatment>> listToDisplay) {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); // Clear existing rows

        if (listToDisplay != null) {
            for (Pair<String, Treatment> pair : listToDisplay) {
                Treatment t = pair.getValue();
                model.addRow(new Object[]{
                    t.getTreatmentID(),
                    t.getConsultation().getPatient().getPatientID(),
                    t.getConsultation().getPatient().getPatientName(),
                    t.getConsultation().getDoctor().getDoctorID(),
                    t.getConsultation().getDoctor().getName(),
                    t.getDiagnosis(),
                    t.getTreatmentDetails(),
                    t.getNotes(),
                    t.getFormattedDateTime()
                });
            }
        }
    }
    
    // Filters the table based on the search input using the control's filter method
    private void filterTable() {
        String query = searchInput.getText().trim();
        if (query.isEmpty()) {
            populateTable(masterTreatmentList);
            return;
        }

        // Use the control's filter method which searches across Patient Name, Doctor Name, and Diagnosis
        // We'll search across multiple criteria to maintain the same functionality as before
        DoublyLinkedList<Pair<String, Treatment>> searchResults = new DoublyLinkedList<>();
        
        // Search in Patient Name
        DoublyLinkedList<Pair<String, Treatment>> patientResults = treatmentControl.filterTreatments("Patient Name", query);
        for (Pair<String, Treatment> pair : patientResults) {
            searchResults.insertLast(pair);
        }
        
        // Search in Doctor Name
        DoublyLinkedList<Pair<String, Treatment>> doctorResults = treatmentControl.filterTreatments("Doctor Name", query);
        for (Pair<String, Treatment> pair : doctorResults) {
            // Avoid duplicates
            boolean exists = false;
            for (Pair<String, Treatment> existing : searchResults) {
                if (existing.getKey().equals(pair.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                searchResults.insertLast(pair);
            }
        }
        
        // Search in Diagnosis
        DoublyLinkedList<Pair<String, Treatment>> diagnosisResults = treatmentControl.filterTreatments("Diagnosis", query);
        for (Pair<String, Treatment> pair : diagnosisResults) {
            // Avoid duplicates
            boolean exists = false;
            for (Pair<String, Treatment> existing : searchResults) {
                if (existing.getKey().equals(pair.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                searchResults.insertLast(pair);
            }
        }
        
        populateTable(searchResults);
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
        searchInput = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortComboBox = new javax.swing.JComboBox<>();
        historyTablePanel = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        report1Button = new javax.swing.JButton();
        report2Button = new javax.swing.JButton();
        popularReportButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Treatment History");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        filterLabel.setText("Search by Patient/Doctor/Diagnosis:");
        searchPanel.add(filterLabel);

        searchInput.setColumns(15);
        searchInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchInputActionPerformed(evt);
            }
        });
        searchPanel.add(searchInput);

        sortLabel.setText("Sort By :");
        searchPanel.add(sortLabel);

        sortComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Date (Newest First)", "Date (Oldest First)", "Cost (Highest First)", "Cost (Lowest First)" }));
        sortComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortComboBoxActionPerformed(evt);
            }
        });
        searchPanel.add(sortComboBox);

        searchWrapperPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        historyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTablePanel.setViewportView(historyTable);

        searchWrapperPanel.add(historyTablePanel, java.awt.BorderLayout.CENTER);

        editButton.setText("Edit a Treatment");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(editButton);

        report1Button.setText("Common Diagnoses Report");
        report1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                report1ButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(report1Button);

        report2Button.setText("Patient History Report");
        report2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                report2ButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(report2Button);

        popularReportButton.setText("Popular Treatment Report");
        popularReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popularReportButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(popularReportButton);

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(backButton);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchInputActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        TreatmentEditDialog dialog = new TreatmentEditDialog(mainFrame, true, treatmentControl);
        dialog.setVisible(true);
        loadAndDisplayTreatments(); // Refresh table after edit dialog closes
    }//GEN-LAST:event_editButtonActionPerformed

    private void report1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_report1ButtonActionPerformed
        // Create a fresh control instance to load the latest data from the file
        TreatmentControl freshControl = new TreatmentControl();
        DoublyLinkedList<Pair<String, Integer>> frequencyData = freshControl.getDiagnosisFrequency();
        
        if (frequencyData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatment data to generate report.", "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ReportGenerator.generateDiagnosisFrequencyReport(frequencyData);
        JOptionPane.showMessageDialog(this, "Report generated: 'Common_Diagnoses_Report.pdf'", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_report1ButtonActionPerformed

    private void report2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_report2ButtonActionPerformed
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID (e.g., P001):");
        if (patientId == null || patientId.trim().isEmpty()) {
            return;
        }
        
        String finalPatientId = patientId.trim().toUpperCase();
        
        // Create a fresh control instance to load the latest data from the file
        TreatmentControl freshControl = new TreatmentControl();
        DoublyLinkedList<Treatment> patientHistory = freshControl.getTreatmentsForPatient(finalPatientId);
        
        if (patientHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatments found for Patient ID: " + finalPatientId, "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ReportGenerator.generatePatientHistoryReport(patientHistory);
        JOptionPane.showMessageDialog(this, "Report generated: 'Patient_History_" + finalPatientId + ".pdf'", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_report2ButtonActionPerformed

    private void sortComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortComboBoxActionPerformed
        String selection = (String) sortComboBox.getSelectedItem();
        if ("Default".equals(selection)) {
            populateTable(masterTreatmentList);
            return;
        }

        String sortBy = selection.contains("Date") ? "Date" : "Cost";
        String order = selection.contains("Oldest") || selection.contains("Lowest") ? "ASC" : "DESC";
        
        DoublyLinkedList<Pair<String, Treatment>> sortedList = treatmentControl.bubbleSortTreatments(masterTreatmentList, sortBy, order);
        populateTable(sortedList);
    }//GEN-LAST:event_sortComboBoxActionPerformed

    private void popularReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popularReportButtonActionPerformed
        loadAndDisplayTreatments(); // Ensure data is fresh before generating report
        if (masterTreatmentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatment data to generate report.", "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ReportGenerator.generateTreatmentTypePopularityReport(masterTreatmentList);
        JOptionPane.showMessageDialog(this, "Report generated: 'Treatment_Type_Popularity_Report.pdf'", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_popularReportButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.showPanel("medicalManagement");
    }//GEN-LAST:event_backButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTable historyTable;
    private javax.swing.JScrollPane historyTablePanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton popularReportButton;
    private javax.swing.JButton report1Button;
    private javax.swing.JButton report2Button;
    private javax.swing.JTextField searchInput;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JComboBox<String> sortComboBox;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
