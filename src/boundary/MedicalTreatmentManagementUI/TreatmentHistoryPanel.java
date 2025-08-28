/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.MedicalTreatmentManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import control.TreatmentControl;
import enitity.Treatment;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
    
    // UI Components declared as instance variables
    private JTable historyTable;
    private JTextField searchInput;
    private JComboBox<String> sortComboBox;

    /**
     * Creates new form TreatmentHistoryPanel
     * @param mainFrame
     */
    public TreatmentHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents(); // Keep this for basic panel setup
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Build the UI programmatically
        initializeHistoryInterface();
        
        // Load initial data
        loadAndDisplayTreatments();
    }
    
    // This method replaces the auto-generated GUI code with a programmatic approach
    private void initializeHistoryInterface() {
        // Main content panel that will hold everything
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Controls Panel (Top) for search and sort
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        controlsPanel.add(new JLabel("Search by Patient/Doctor/Diagnosis:"));
        searchInput = new JTextField(20);
        controlsPanel.add(searchInput);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchButtonActionPerformed(null));
        controlsPanel.add(searchButton);

        controlsPanel.add(new JLabel("Sort By:"));
        sortComboBox = new JComboBox<>(new String[]{ "Default", "Date (Newest First)", "Date (Oldest First)", "Cost (Highest First)", "Cost (Lowest First)" });
        sortComboBox.addActionListener(e -> sortComboBoxActionPerformed(null));
        controlsPanel.add(sortComboBox);

        contentPanel.add(controlsPanel, BorderLayout.NORTH);

        // 2. Table Panel (Center) - UPDATED with new columns
        String[] columnNames = {"Treatment ID", "Patient ID", "Patient Name", "Doctor ID", "Doctor Name", "Diagnosis", "Treatment Details", "Cost (RM)", "Date & Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is not editable
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScrollPane = new JScrollPane(historyTable);

        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 3. Button Panel (Bottom) for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        // NEW "Edit Treatment" button
        JButton editButton = new JButton("Edit a Treatment");
        editButton.addActionListener(e -> editButtonActionPerformed(null));
        buttonPanel.add(editButton);
        
        JButton report1Button = new JButton("Common Diagnoses Report");
        report1Button.addActionListener(e -> report1ButtonActionPerformed(null));
        buttonPanel.add(report1Button);
        JButton report2Button = new JButton("Patient History Report");
        report2Button.addActionListener(e -> report2ButtonActionPerformed(null));
        buttonPanel.add(report2Button);
        JButton costReportButton = new JButton("Treatment Cost Analysis");
        costReportButton.addActionListener(e -> costReportButtonActionPerformed(null));
        buttonPanel.add(costReportButton);
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshButtonActionPerformed(null));
        buttonPanel.add(refreshButton);
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> backButtonActionPerformed(null));
        buttonPanel.add(backButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        titlePanel.add(contentPanel, BorderLayout.CENTER);

        // Add the fully constructed content panel to the main title panel
        titlePanel.add(contentPanel, BorderLayout.CENTER);
    }

    // Loads data from the file and populates the table
    public void loadAndDisplayTreatments() {
        masterTreatmentList = (DoublyLinkedList<Pair<String, Treatment>>) FileUtils.readDataFromFile("treatments");
        if (masterTreatmentList == null) {
            masterTreatmentList = new DoublyLinkedList<>();
        }
        populateTable(masterTreatmentList);
    }
    
    // Helper method to populate the table with a given list
    private void populateTable(DoublyLinkedList<Pair<String, Treatment>> listToDisplay) {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); // Clear existing rows

        if (listToDisplay != null) {
            for (Pair<String, Treatment> pair : listToDisplay) {
                Treatment t = pair.getValue();
                // UPDATED with new data for the ID columns
                model.addRow(new Object[]{
                    t.getTreatmentID(),
                    t.getConsultation().getPatient().getPatientID(),
                    t.getConsultation().getPatient().getPatientName(),
                    t.getConsultation().getDoctor().getDoctorID(),
                    t.getConsultation().getDoctor().getName(),
                    t.getDiagnosis(),
                    t.getTreatmentDetails(),
                    String.format("%.2f", t.getCost()),
                    t.getFormattedDateTime()
                });
            }
        }
    }
    
    // UPDATED ACTION LISTENER
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Create and show the edit dialog. No need to select a row first.
        TreatmentEditDialog dialog = new TreatmentEditDialog(mainFrame, true, treatmentControl);
        dialog.setVisible(true);

        // After the dialog is closed, refresh the table to show any changes
        loadAndDisplayTreatments();
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        mainFrame.showPanel("medicalManagement");
    }                                          

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        searchInput.setText("");
        sortComboBox.setSelectedIndex(0);
        loadAndDisplayTreatments();
    }                                             

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        String query = searchInput.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(masterTreatmentList);
            return;
        }

        DoublyLinkedList<Pair<String, Treatment>> searchResults = new DoublyLinkedList<>();
        for (Pair<String, Treatment> pair : masterTreatmentList) {
            Treatment t = pair.getValue();
            boolean match = t.getConsultation().getPatient().getPatientName().toLowerCase().contains(query) ||
                            t.getConsultation().getDoctor().getName().toLowerCase().contains(query) ||
                            t.getDiagnosis().toLowerCase().contains(query);
            if (match) {
                searchResults.insertLast(pair);
            }
        }
        populateTable(searchResults);
    }                                            

    private void sortComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                             
        String selection = (String) sortComboBox.getSelectedItem();
        if ("Default".equals(selection)) {
            populateTable(masterTreatmentList);
            return;
        }

        String sortBy = selection.contains("Date") ? "Date" : "Cost";
        String order = selection.contains("Oldest") || selection.contains("Lowest") ? "ASC" : "DESC";
        
        DoublyLinkedList<Pair<String, Treatment>> sortedList = treatmentControl.bubbleSortTreatments(masterTreatmentList, sortBy, order);
        populateTable(sortedList);
    }                                            

    private void report1ButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        DoublyLinkedList<Pair<String, Integer>> frequencyData = treatmentControl.getDiagnosisFrequency();
        if (frequencyData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatment data available to generate report.", "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ReportGenerator.generateDiagnosisFrequencyReport(frequencyData);
        JOptionPane.showMessageDialog(this, "Common Diagnoses Report generated successfully! (Check project folder for PDF)", "Success", JOptionPane.INFORMATION_MESSAGE);
    }                                             

    private void report2ButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to generate history report (e.g., P001):");
        if (patientId == null || patientId.trim().isEmpty()) {
            return;
        }
        
        DoublyLinkedList<Treatment> patientHistory = treatmentControl.getTreatmentsForPatient(patientId.trim());
        if (patientHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatments found for Patient ID: " + patientId, "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ReportGenerator.generatePatientHistoryReport(patientHistory);
        JOptionPane.showMessageDialog(this, "Patient History Report generated successfully! (Check project folder for PDF)", "Success", JOptionPane.INFORMATION_MESSAGE);
    }                                            
    
    private void costReportButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        if (masterTreatmentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No treatment data available to generate report.", "Report Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ReportGenerator.generateTreatmentCostReport(masterTreatmentList);
        JOptionPane.showMessageDialog(this, "Treatment Cost Analysis Report generated successfully! (Check project folder for PDF)", "Success", JOptionPane.INFORMATION_MESSAGE);
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

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Treatment History");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
