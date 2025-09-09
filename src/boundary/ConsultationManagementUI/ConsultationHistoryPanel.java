package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.Consultation;
import enitity.Patient;
import enitity.Doctor;
import adt.DoublyLinkedList;
import adt.Pair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import utility.FileUtils;
import utility.ImageUtils;

/**
 * Consultation History Panel
 * @author Zhen Bang
 */
public class ConsultationHistoryPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private JTable historyTable;
    private JScrollPane tableScrollPane;
    private JPanel dataPanel;
    private JComboBox<String> patientFilterComboBox;
    private JComboBox<String> doctorFilterComboBox;
    private JComboBox<String> statusFilterComboBox;
    private JComboBox<String> typeFilterComboBox;
    private JTextField searchField;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private DoublyLinkedList<Pair<String, Consultation>> allConsultations;
    
    /**
     * Creates new form ConsultationHistoryPanel
     */
    public ConsultationHistoryPanel(MainFrame mainFrame) {
        this(mainFrame, new ConsultationControl());
    }
    
    /**
     * Creates new form ConsultationHistoryPanel with consultation control
     */
    public ConsultationHistoryPanel(MainFrame mainFrame, ConsultationControl consultationControl) {
        this.mainFrame = mainFrame;
        this.consultationControl = consultationControl;
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Initialize the consultation history interface
        initializeHistoryInterface();
        loadConsultationHistory();
    }

    public void reloadData() {
        loadConsultationHistory();
        populateFilterComboBoxes();
    }
    
    private void initializeHistoryInterface() {
        // Create the data panel
        dataPanel = new JPanel(new BorderLayout());
        
        // Create the table
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Symptoms", "Diagnosis", "Notes"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getTableHeader().setReorderingAllowed(false);
        
        tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        
        // Create filter panel
        JPanel filterPanel = createFilterPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(filterPanel, BorderLayout.NORTH);
        mainContentPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main content directly (no global scrolling)
        dataPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Ensure content panel fills small windows nicely
        contentPanel.setLayout(new java.awt.BorderLayout());
        // Add data panel to content panel
        contentPanel.add(dataPanel, java.awt.BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filter Options"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        
        // Initialize components
        patientFilterComboBox = new JComboBox<>();
        doctorFilterComboBox = new JComboBox<>();
        statusFilterComboBox = new JComboBox<>();
        typeFilterComboBox = new JComboBox<>();
        searchField = new JTextField(20);
        
        // Setup date spinners
        setupDateSpinners();
        
        // Search field spans full width at top
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchField, gbc);
        
        // Left column: Patient, Doctor, Status
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(patientFilterComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(doctorFilterComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusFilterComboBox, gbc);
        
        // Right column: Type, From Date, To Date
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(typeFilterComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("From Date:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fromDateSpinner, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("To Date:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toDateSpinner, gbc);
        
        // Populate filter combo boxes
        populateFilterComboBoxes();
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton filterButton = new JButton("Apply Filters");
        JButton clearButton = new JButton("Clear Filters");
        JButton exportButton = new JButton("Export to CSV");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        
        filterButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> clearFilters());
        exportButton.addActionListener(e -> exportToCSV());
        refreshButton.addActionListener(e -> refreshData());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(filterButton);
        panel.add(clearButton);
        panel.add(exportButton);
        panel.add(refreshButton);
        panel.add(backButton);
        
        return panel;
    }
    
    private void setupDateSpinners() {
        // Create from date spinner
        SpinnerDateModel fromDateModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromDateModel);
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy");
        fromDateSpinner.setEditor(fromDateEditor);
        
        // Create to date spinner
        SpinnerDateModel toDateModel = new SpinnerDateModel();
        toDateSpinner = new JSpinner(toDateModel);
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy");
        toDateSpinner.setEditor(toDateEditor);
        
        // Set default dates (last 30 days)
        Calendar calendar = Calendar.getInstance();
        toDateSpinner.setValue(calendar.getTime());
        
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        fromDateSpinner.setValue(calendar.getTime());
        
        // Style the spinners
        fromDateSpinner.setPreferredSize(new Dimension(120, 25));
        toDateSpinner.setPreferredSize(new Dimension(120, 25));
    }
    
    private void populateFilterComboBoxes() {
        patientFilterComboBox.removeAllItems();
        doctorFilterComboBox.removeAllItems();
        statusFilterComboBox.removeAllItems();
        typeFilterComboBox.removeAllItems();
        
        // Add "All" option to all filters
        patientFilterComboBox.addItem("All Patients");
        doctorFilterComboBox.addItem("All Doctors");
        statusFilterComboBox.addItem("All Statuses");
        typeFilterComboBox.addItem("All Types");
        
        // Load and populate patients
        DoublyLinkedList<Patient> patients = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
        if (patients != null) {
            for (Patient patient : patients) {
                patientFilterComboBox.addItem(patient.getPatientID() + " - " + patient.getPatientName());
            }
        }
        
        // Load and populate doctors
        DoublyLinkedList<Pair<String, Doctor>> doctors = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (doctors != null) {
            for (Pair<String, Doctor> pair : doctors) {
                Doctor doctor = pair.getValue();
                doctorFilterComboBox.addItem(doctor.getDoctorID() + " - " + doctor.getName());
            }
        }
        
        // Add statuses
        statusFilterComboBox.addItem("Scheduled");
        statusFilterComboBox.addItem("In Progress");
        statusFilterComboBox.addItem("Completed");
        statusFilterComboBox.addItem("Cancelled");
        
        // Add types
        typeFilterComboBox.addItem("New Visit");
        typeFilterComboBox.addItem("Follow-up");
        typeFilterComboBox.addItem("Emergency");
    }
    
    private void loadConsultationHistory() {
        if (historyTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        
        allConsultations = consultationControl.getAllConsultations();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Pair<String, Consultation> pair : allConsultations) {
            Consultation consultation = pair.getValue();
            String date = consultation.getConsultationDateTime() != null ? 
                         consultation.getConsultationDateTime().format(fmt) : "-";
            
            model.addRow(new Object[]{
                consultation.getConsultationID(),
                consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "-",
                consultation.getDoctor() != null ? consultation.getDoctor().getName() : "-",
                date,
                consultation.getConsultationType(),
                consultation.getStatus(),
                consultation.getSymptoms(),
                consultation.getDiagnosis() != null ? consultation.getDiagnosis() : "-",
                consultation.getNotes() != null ? consultation.getNotes() : "-"
            });
        }
    }
    
    private void applyFilters() {
        if (historyTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedPatient = (String) patientFilterComboBox.getSelectedItem();
        String selectedDoctor = (String) doctorFilterComboBox.getSelectedItem();
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        String selectedType = (String) typeFilterComboBox.getSelectedItem();
        
        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Pair<String, Consultation> pair : allConsultations) {
            Consultation consultation = pair.getValue();
            // Apply filters
            if (!matchesSearch(consultation, searchText)) continue;
            if (!matchesPatient(consultation, selectedPatient)) continue;
            if (!matchesDoctor(consultation, selectedDoctor)) continue;
            if (!matchesStatus(consultation, selectedStatus)) continue;
            if (!matchesType(consultation, selectedType)) continue;
            if (!matchesDateRange(consultation, fromDate, toDate)) continue;
            
            // Add to table if all filters pass
            String date = consultation.getConsultationDateTime() != null ? 
                         consultation.getConsultationDateTime().format(fmt) : "-";
            
            model.addRow(new Object[]{
                consultation.getConsultationID(),
                consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "-",
                consultation.getDoctor() != null ? consultation.getDoctor().getName() : "-",
                date,
                consultation.getConsultationType(),
                consultation.getStatus(),
                consultation.getSymptoms(),
                consultation.getDiagnosis() != null ? consultation.getDiagnosis() : "-",
                consultation.getNotes() != null ? consultation.getNotes() : "-"
            });
        }
        
        JOptionPane.showMessageDialog(this, "Filters applied. Showing " + model.getRowCount() + " consultations.", 
                                    "Filter Applied", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean matchesSearch(Consultation consultation, String searchText) {
        if (searchText.isEmpty()) return true;
        
        String patientName = consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "";
        String doctorName = consultation.getDoctor() != null ? consultation.getDoctor().getName() : "";
        String symptoms = consultation.getSymptoms() != null ? consultation.getSymptoms() : "";
        String diagnosis = consultation.getDiagnosis() != null ? consultation.getDiagnosis() : "";
        String notes = consultation.getNotes() != null ? consultation.getNotes() : "";
        
        return patientName.toLowerCase().contains(searchText) ||
               doctorName.toLowerCase().contains(searchText) ||
               symptoms.toLowerCase().contains(searchText) ||
               diagnosis.toLowerCase().contains(searchText) ||
               notes.toLowerCase().contains(searchText) ||
               consultation.getConsultationID().toLowerCase().contains(searchText);
    }
    
    private boolean matchesPatient(Consultation consultation, String selectedPatient) {
        if ("All Patients".equals(selectedPatient)) return true;
        if (consultation.getPatient() == null) return false;
        
        String patientID = selectedPatient.split(" - ")[0];
        return consultation.getPatient().getPatientID().equals(patientID);
    }
    
    private boolean matchesDoctor(Consultation consultation, String selectedDoctor) {
        if ("All Doctors".equals(selectedDoctor)) return true;
        if (consultation.getDoctor() == null) return false;
        
        String doctorID = selectedDoctor.split(" - ")[0];
        return consultation.getDoctor().getDoctorID().equals(doctorID);
    }
    
    private boolean matchesStatus(Consultation consultation, String selectedStatus) {
        if ("All Statuses".equals(selectedStatus)) return true;
        return consultation.getStatus().equals(selectedStatus);
    }
    
    private boolean matchesType(Consultation consultation, String selectedType) {
        if ("All Types".equals(selectedType)) return true;
        return consultation.getConsultationType().equals(selectedType);
    }
    
    private boolean matchesDateRange(Consultation consultation, Date fromDate, Date toDate) {
        if (consultation.getConsultationDateTime() == null) return false;
        
        LocalDateTime consultationDate = consultation.getConsultationDateTime();
        LocalDateTime fromDateTime = fromDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toDateTime = toDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        
        return !consultationDate.isBefore(fromDateTime) && !consultationDate.isAfter(toDateTime);
    }
    
    private void clearFilters() {
        searchField.setText("");
        patientFilterComboBox.setSelectedIndex(0);
        doctorFilterComboBox.setSelectedIndex(0);
        statusFilterComboBox.setSelectedIndex(0);
        typeFilterComboBox.setSelectedIndex(0);
        
        // Reset date range to last 30 days
        Calendar calendar = Calendar.getInstance();
        toDateSpinner.setValue(calendar.getTime());
        
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        fromDateSpinner.setValue(calendar.getTime());
        
        loadConsultationHistory();
        JOptionPane.showMessageDialog(this, "All filters cleared.", "Filters Cleared", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Consultation History to CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new java.io.File("consultation_history.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                exportConsultationsToCSV(file);
                JOptionPane.showMessageDialog(this, "Consultation history exported successfully to " + file.getName(), 
                                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + e.getMessage(), 
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportConsultationsToCSV(java.io.File file) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Write header
            writer.println("ID,Patient,Doctor,Date/Time,Type,Status,Symptoms,Diagnosis,Notes");
            
            // Write data
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    String cellValue = value != null ? value.toString().replace("\"", "\"\"") : "";
                    line.append("\"").append(cellValue).append("\"");
                    if (j < model.getColumnCount() - 1) {
                        line.append(",");
                    }
                }
                writer.println(line.toString());
            }
        }
    }
    
    private void refreshData() {
        loadConsultationHistory();
        JOptionPane.showMessageDialog(this, "Data refreshed successfully.", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Consultation History");
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.FlowLayout());

        titlePanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
