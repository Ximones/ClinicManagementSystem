package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationControl;
import enitity.Consultation;
import enitity.Patient;
import enitity.Doctor;
import enitity.QueueEntry;
import enitity.Prescription;
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
 * Consultation Management Panel
 * @author Zhen Bang
 */
public class ConsultationPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private JTable consultationTable;
    private JScrollPane tableScrollPane;
    private JPanel dataPanel;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JComboBox<String> consultationTypeComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField symptomsField;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private QueuePanel queuePanel; // Reference to queue panel for refreshing
    
    /**
     * Creates new form ConsultationPanel
     */
    public ConsultationPanel(MainFrame mainFrame) {
        this(mainFrame, null);
    }

    /**
     * Creates new form ConsultationPanel with queue panel reference
     */
    public ConsultationPanel(MainFrame mainFrame, QueuePanel queuePanel) {
        this(mainFrame, queuePanel, new ConsultationControl());
    }
    
    /**
     * Creates new form ConsultationPanel with queue panel and consultation control references
     */
    public ConsultationPanel(MainFrame mainFrame, QueuePanel queuePanel, ConsultationControl consultationControl) {
        this.mainFrame = mainFrame;
        this.queuePanel = queuePanel;
        this.consultationControl = consultationControl;
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Initialize the consultation management interface
        initializeConsultationInterface();
        loadConsultations();
    }
    
    /**
     * Refreshes the consultation panel display
     */
    public void refreshConsultationDisplay() {
        loadConsultations();
        updateCurrentConsultingPatient();
    }

    /**
     * Starts consultation for a specific patient from the queue
     * @param patient The patient to start consultation for
     */
    public void startConsultationForPatient(Patient patient) {
        if (patient == null) return;
        
        // Pre-select the patient in the combo box
        for (int i = 0; i < patientComboBox.getItemCount(); i++) {
            String item = patientComboBox.getItemAt(i);
            if (item.contains(patient.getPatientID())) {
                patientComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Set consultation type to "New Visit" by default
        consultationTypeComboBox.setSelectedItem("New Visit");
        
        // Set current date and time
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        // Focus on symptoms field
        symptomsField.requestFocus();
    }

    // Public method to re-read all required data and refresh UI when panel is shown
    public void reloadData() {
        loadConsultations();
        populateComboBoxes();
        updateCurrentConsultingPatient();
    }
    
    private void initializeConsultationInterface() {
        // Create the data panel
        dataPanel = new JPanel(new BorderLayout());
        
        // Create the table
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Symptoms"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        consultationTable = new JTable(tableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationTable.getTableHeader().setReorderingAllowed(false);
        
        tableScrollPane = new JScrollPane(consultationTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 250));
        
        // Create input panel
        JPanel inputPanel = createInputPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(inputPanel, BorderLayout.NORTH);
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
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Consultation Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        
        // Initialize components
        patientComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        consultationTypeComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>();
        symptomsField = new JTextField(20);
        
        // Setup date/time spinners
        setupDateTimeSpinners();
        
        // Left column: Patient, Doctor, Date
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(patientComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(doctorComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateSpinner, gbc);
        
        // Right column: Type, Symptoms, Status
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(consultationTypeComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Symptoms:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(symptomsField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusComboBox, gbc);
        
        // Populate combo boxes
        populateComboBoxes();
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Consultation");
        JButton updateButton = new JButton("Update Consultation");
        JButton deleteButton = new JButton("Delete Consultation");
        JButton toPrescriptionButton = new JButton("To Prescription");
        JButton backButton = new JButton("Back");
        
        addButton.addActionListener(e -> addConsultation());
        updateButton.addActionListener(e -> updateConsultation());
        deleteButton.addActionListener(e -> deleteConsultation());
        toPrescriptionButton.addActionListener(e -> moveToPrescription());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(toPrescriptionButton);
        panel.add(backButton);
        
        return panel;
    }
    
    private void setupDateTimeSpinners() {
        // Create date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        // Create time spinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        
        // Set current date and time as default
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        // Style the spinners
        dateSpinner.setPreferredSize(new Dimension(120, 25));
        timeSpinner.setPreferredSize(new Dimension(80, 25));
    }
    
    private void populateComboBoxes() {
        patientComboBox.removeAllItems();
        doctorComboBox.removeAllItems();
        consultationTypeComboBox.removeAllItems();
        statusComboBox.removeAllItems();
        
        // Load and populate patients
        DoublyLinkedList<Patient> patients = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
        if (patients != null) {
            for (Patient patient : patients) {
                patientComboBox.addItem(patient.getPatientID() + " - " + patient.getPatientName());
            }
        }
        
        // Load and populate doctors
        DoublyLinkedList<Pair<String, Doctor>> doctors = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (doctors != null) {
            for (Pair<String, Doctor> pair : doctors) {
                Doctor doctor = pair.getValue();
                doctorComboBox.addItem(doctor.getDoctorID() + " - " + doctor.getName());
            }
        }
        
        // Add consultation types
        consultationTypeComboBox.addItem("New Visit");
        consultationTypeComboBox.addItem("Follow-up");
        consultationTypeComboBox.addItem("Emergency");
        
        // Add statuses (kept for display) but disable manual change; automation will set it
        statusComboBox.addItem("Scheduled");
        statusComboBox.addItem("In Progress");
        statusComboBox.addItem("Completed");
        statusComboBox.addItem("Cancelled");
        statusComboBox.setEnabled(false);
    }
    
    private void loadConsultations() {
        if (consultationTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) consultationTable.getModel();
        model.setRowCount(0);
        
        List<Consultation> consultations = consultationControl.getAllConsultations();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Consultation consultation : consultations) {
            String date = consultation.getConsultationDateTime() != null ? 
                         consultation.getConsultationDateTime().format(fmt) : "-";
            
            model.addRow(new Object[]{
                consultation.getConsultationID(),
                consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "-",
                consultation.getDoctor() != null ? consultation.getDoctor().getName() : "-",
                date,
                consultation.getConsultationType(),
                consultation.getStatus(),
                consultation.getSymptoms()
            });
        }
    }
    
    private Patient getSelectedPatient() {
        String selected = (String) patientComboBox.getSelectedItem();
        if (selected != null) {
            String patientID = selected.split(" - ")[0];
            DoublyLinkedList<Patient> patients = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
            if (patients != null) {
                for (Patient patient : patients) {
                    if (patient.getPatientID().equals(patientID)) {
                        return patient;
                    }
                }
            }
        }
        return null;
    }
    
    private Doctor getSelectedDoctor() {
        String selected = (String) doctorComboBox.getSelectedItem();
        if (selected != null) {
            String doctorID = selected.split(" - ")[0];
            DoublyLinkedList<Pair<String, Doctor>> doctors = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
            if (doctors != null) {
                for (Pair<String, Doctor> pair : doctors) {
                    if (pair.getKey().equals(doctorID)) {
                        return pair.getValue();
                    }
                }
            }
        }
        return null;
    }
    
    private LocalDateTime getSelectedDateTime() {
        Date date = (Date) dateSpinner.getValue();
        Date time = (Date) timeSpinner.getValue();
        
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        Calendar combined = Calendar.getInstance();
        combined.set(dateCal.get(Calendar.YEAR), dateCal.get(Calendar.MONTH), dateCal.get(Calendar.DAY_OF_MONTH),
                    timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), 0);
        
        return LocalDateTime.ofInstant(combined.toInstant(), combined.getTimeZone().toZoneId());
    }

    private void updateCurrentConsultingPatient() {
        // Load shared queue and find current consulting
        QueueEntry currentConsulting = null;
        
        try {
            // Try to load in new format (Pair<String, QueueEntry>)
            DoublyLinkedList<Pair<String, QueueEntry>> queueListPair = 
                (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
            
            if (queueListPair != null) {
                for (Pair<String, QueueEntry> pair : queueListPair) {
                    QueueEntry entry = pair.getValue();
                    if ("Consulting".equals(entry.getStatus())) {
                        currentConsulting = entry;
                        break;
                    }
                }
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            try {
                DoublyLinkedList<QueueEntry> queueList = 
                    (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
                
                if (queueList != null) {
                    for (QueueEntry entry : queueList) {
                        if ("Consulting".equals(entry.getStatus())) {
                            currentConsulting = entry;
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error loading queue data in updateCurrentConsultingPatient: " + ex.getMessage());
            }
        }
        
        if (currentConsulting != null) {
            Patient patient = currentConsulting.getPatient();
            String targetId = patient != null ? patient.getPatientID() : null;
            if (targetId != null) {
                for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                    String item = patientComboBox.getItemAt(i);
                    if (item.startsWith(targetId + " ")) {
                        patientComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                // Lock patient selection during active consultation
                patientComboBox.setEnabled(false);
            }
        } else {
            // No active consulting patient; allow normal selection
            patientComboBox.setEnabled(true);
        }
    }
    
    private void addConsultation() {
        try {
            Patient patient = getSelectedPatient();
            Doctor doctor = getSelectedDoctor();
            
            if (patient == null || doctor == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDateTime consultationDateTime = getSelectedDateTime();
            String consultationType = (String) consultationTypeComboBox.getSelectedItem();
            String symptoms = symptomsField.getText().trim();
            
            if (symptoms.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter symptoms.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Consultation consultation = consultationControl.createConsultation(
                patient, doctor, consultationDateTime, consultationType, symptoms);
            
            JOptionPane.showMessageDialog(this, "Consultation created successfully!\nID: " + consultation.getConsultationID(), 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearFields();
            loadConsultations();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating consultation: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateConsultation() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String consultationID = (String) consultationTable.getValueAt(selectedRow, 0);
        String newStatus = (String) statusComboBox.getSelectedItem();
        
        boolean updated = consultationControl.updateConsultationStatus(consultationID, newStatus);
        
        if (updated) {
            JOptionPane.showMessageDialog(this, "Consultation status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadConsultations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update consultation status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteConsultation() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String consultationID = (String) consultationTable.getValueAt(selectedRow, 0);
        String patientName = (String) consultationTable.getValueAt(selectedRow, 1);
        
        // Check for related prescriptions
        List<Prescription> relatedPrescriptions = consultationControl.getPrescriptionsByConsultation(consultationID);
        boolean hasRelatedPrescriptions = !relatedPrescriptions.isEmpty();
        
        StringBuilder confirmMessage = new StringBuilder();
        confirmMessage.append("Are you sure you want to delete this consultation?\n\n");
        confirmMessage.append("Consultation ID: ").append(consultationID).append("\n");
        confirmMessage.append("Patient: ").append(patientName).append("\n\n");
        
        if (hasRelatedPrescriptions) {
            confirmMessage.append("WARNING: This consultation has ").append(relatedPrescriptions.size())
                         .append(" related prescription(s) that will also be deleted:\n");
            for (Prescription prescription : relatedPrescriptions) {
                confirmMessage.append("  - Prescription ID: ").append(prescription.getPrescriptionID()).append("\n");
            }
            confirmMessage.append("\n");
        }
        
        confirmMessage.append("This action cannot be undone.");
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage.toString(), 
            "Confirm Delete Consultation", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = consultationControl.deleteConsultation(consultationID);
            
            if (deleted) {
                StringBuilder successMessage = new StringBuilder();
                successMessage.append("Consultation deleted successfully!\n\n");
                successMessage.append("Consultation ID: ").append(consultationID).append("\n");
                successMessage.append("Patient: ").append(patientName).append("\n");
                
                if (hasRelatedPrescriptions) {
                    successMessage.append("\nRelated prescriptions were also deleted.");
                }
                
                JOptionPane.showMessageDialog(this, 
                    successMessage.toString(), 
                    "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                loadConsultations();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete consultation.\n" +
                    "Consultation ID: " + consultationID + "\n\n" +
                    "The consultation may not exist or may have already been deleted.", 
                    "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moveToPrescription() {
        // Move the current consulting patient to prescribing and open prescription panel
        QueueEntry currentConsulting = consultationControl.getCurrentConsultingPatient();
        if (currentConsulting == null) {
            JOptionPane.showMessageDialog(this, "No patient currently consulting.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        currentConsulting.markPrescriptioning();
        consultationControl.saveData();
        mainFrame.showPanel("prescriptionPanel");
        JOptionPane.showMessageDialog(this,
                "Moved to prescription for " + (currentConsulting.getPatient() != null ? currentConsulting.getPatient().getPatientName() : "patient") + ".",
                "Proceed to Prescription", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearFields() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        consultationTypeComboBox.setSelectedIndex(0);
        symptomsField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(700, 500));
        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Consultation Management");
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.FlowLayout());

        titlePanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("consultationManagement");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
} 
