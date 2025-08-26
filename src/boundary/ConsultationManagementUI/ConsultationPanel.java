/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationControl;
import enitity.Consultation;
import enitity.Patient;
import enitity.Doctor;
import enitity.QueueEntry;
import adt.DoublyLinkedList;
import adt.Pair;
import java.time.LocalDateTime;
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
    private DefaultTableModel consultationTableModel;
    private DoublyLinkedList<Pair<String, Patient>> patientList;
    private DoublyLinkedList<Pair<String, Doctor>> doctorList;
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
        this.mainFrame = mainFrame;
        this.queuePanel = queuePanel;
        this.consultationControl = new ConsultationControl();
        
        // Initialize spinners before layout setup
        setupDateTimeSpinners();
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        initializeData();
        loadConsultations();
        
        // Debug: Print initial state
        System.out.println("=== ConsultationPanel Initialized ===");
        System.out.println("Patient combo box items: " + patientComboBox.getItemCount());
        System.out.println("Doctor combo box items: " + doctorComboBox.getItemCount());
        System.out.println("Patient list size: " + patientList.getSize());
        System.out.println("Doctor list size: " + doctorList.getSize());
        
        // Test method to verify data loading
        testDataLoading();
    }
    
    /**
     * Refreshes the consultation panel display
     */
    public void refreshConsultationDisplay() {
        loadConsultations();
        updateCurrentConsultingPatient();
    }

    // Public method to re-read all required data and refresh UI when panel is shown
    public void reloadData() {
        initializeData();
        loadConsultations();
        updateCurrentConsultingPatient();
    }
    
    
    
    private void setupTable() {
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Symptoms"};
        consultationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        consultationTable.setModel(consultationTableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void initializeData() {
        // Load real patient data from file
        loadPatientData();
        
        // Load real doctor data from file
        loadDoctorData();
        
        // Populate combo boxes
        populateComboBoxes();
    }
    
    private void loadPatientData() {
        patientList = new DoublyLinkedList<>();
        
        // Load patients from file
        DoublyLinkedList<Patient> loadedPatients = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
        
        if (loadedPatients != null && !loadedPatients.isEmpty()) {
            System.out.println("Loading " + loadedPatients.getSize() + " patients from file");
            
            // Convert to Pair format for consistency
            for (Patient patient : loadedPatients) {
                String patientID = patient.getPatientID();
                patientList.insertLast(new Pair<>(patientID, patient));
                System.out.println("Loaded patient: " + patientID + " - " + patient.getPatientName());
            }
        } else {
            System.out.println("No patient data found in file. Using empty list.");
        }
    }
    
    private void loadDoctorData() {
        doctorList = new DoublyLinkedList<>();
        
        // Load doctors from file
        DoublyLinkedList<Pair<String, Doctor>> loadedDoctors = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        
        if (loadedDoctors != null && !loadedDoctors.isEmpty()) {
            System.out.println("Loading " + loadedDoctors.getSize() + " doctors from file");
            
            // Use the loaded doctors directly since they're already in Pair format
            for (Pair<String, Doctor> doctorPair : loadedDoctors) {
                doctorList.insertLast(doctorPair);
                System.out.println("Loaded doctor: " + doctorPair.getKey() + " - " + doctorPair.getValue().getName());
            }
        } else {
            System.out.println("No doctor data found in file. Using empty list.");
        }
    }
    
    private void populateComboBoxes() {
        patientComboBox.removeAllItems();
        doctorComboBox.removeAllItems();
        consultationTypeComboBox.removeAllItems();
        statusComboBox.removeAllItems();
        
        // Add patients
        System.out.println("Populating patients...");
        for (Pair<String, Patient> pair : patientList) {
            Patient patient = pair.getValue();
            String item = patient.getPatientID() + " - " + patient.getPatientName();
            patientComboBox.addItem(item);
            System.out.println("Added patient: " + item);
        }
        
        // Add doctors
        System.out.println("Populating doctors...");
        for (Pair<String, Doctor> pair : doctorList) {
            Doctor doctor = pair.getValue();
            String item = doctor.getDoctorID() + " - " + doctor.getName();
            doctorComboBox.addItem(item);
            System.out.println("Added doctor: " + item);
        }
        
        // Add consultation types
        consultationTypeComboBox.addItem("New Visit");
        consultationTypeComboBox.addItem("Follow-up");
        consultationTypeComboBox.addItem("Emergency");
        
        // Add simplified statuses
        statusComboBox.addItem("Scheduled");
        statusComboBox.addItem("In Progress");
        statusComboBox.addItem("Completed");
        statusComboBox.addItem("Cancelled");
        
        System.out.println("Combo boxes populated. Patient count: " + patientComboBox.getItemCount() + 
                          ", Doctor count: " + doctorComboBox.getItemCount());
        
        // Set default selections if items are available
        if (patientComboBox.getItemCount() > 0) {
            patientComboBox.setSelectedIndex(0);
        }
        if (doctorComboBox.getItemCount() > 0) {
            doctorComboBox.setSelectedIndex(0);
        }
    }
    
    private void loadConsultations() {
        consultationTableModel.setRowCount(0);
        List<Consultation> consultations = consultationControl.getAllConsultations();
        
        for (Consultation consultation : consultations) {
            Object[] row = {
                consultation.getConsultationID(),
                consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "N/A",
                consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A",
                consultation.getFormattedDateTime(),
                consultation.getConsultationType(),
                consultation.getStatus(),
                consultation.getSymptoms()
            };
            consultationTableModel.addRow(row);
        }
    }
    
    private Patient getSelectedPatient() {
        String selected = (String) patientComboBox.getSelectedItem();
        System.out.println("Selected patient item: " + selected);
        if (selected != null) {
            String patientID = selected.split(" - ")[0];
            System.out.println("Extracted patient ID: " + patientID);
            
            // Debug: Print all patients in the list
            System.out.println("All patients in list:");
            for (Pair<String, Patient> pair : patientList) {
                System.out.println("  Key: " + pair.getKey() + ", Value: " + pair.getValue().getPatientName());
            }
            
            Object result = patientList.findByKey(patientID);
            System.out.println("Found patient result: " + result);
            if (result instanceof Patient) {
                return (Patient) result;
            }
        }
        return null;
    }
    
    private Doctor getSelectedDoctor() {
        String selected = (String) doctorComboBox.getSelectedItem();
        System.out.println("Selected doctor item: " + selected);
        if (selected != null) {
            String doctorID = selected.split(" - ")[0];
            System.out.println("Extracted doctor ID: " + doctorID);
            
            // Debug: Print all doctors in the list
            System.out.println("All doctors in list:");
            for (Pair<String, Doctor> pair : doctorList) {
                System.out.println("  Key: " + pair.getKey() + ", Value: " + pair.getValue().getName());
            }
            
            Object result = doctorList.findByKey(doctorID);
            System.out.println("Found doctor result: " + result);
            if (result instanceof Doctor) {
                return (Doctor) result;
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

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        consultationTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        patientComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        doctorComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        dateTimeField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        consultationTypeComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        symptomsField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(700, 500));

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());
        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Consultation Management");
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        consultationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Symptoms"
            }
        ));
        jScrollPane1.setViewportView(consultationTable);

        addButton.setText("Add Consultation");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Update Consultation");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete Consultation");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        doneButton = new javax.swing.JButton();
        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Consultation Details"));

        jLabel1.setText("Patient:");

        jLabel2.setText("Doctor:");

        jLabel3.setText("Date:");

        jLabel4.setText("Time:");

        jLabel5.setText("Type:");

        jLabel6.setText("Symptoms:");
        
        jLabel7 = new javax.swing.JLabel();
        jLabel7.setText("Status:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(patientComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(doctorComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(dateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(consultationTypeComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(symptomsField)
                    .addComponent(statusComboBox, 0, 200, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(patientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(doctorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(consultationTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(symptomsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.JPanel contentPanel = new javax.swing.JPanel();
        contentPanel.setLayout(new javax.swing.GroupLayout(contentPanel));
        javax.swing.GroupLayout layout = (javax.swing.GroupLayout) contentPanel.getLayout();
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(doneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(doneButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        titlePanel.add(contentPanel, java.awt.BorderLayout.CENTER);
        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            // Check if combo boxes have items
            if (patientComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No patients available. Please ensure patient data is loaded.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (doctorComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No doctors available. Please ensure doctor data is loaded.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = getSelectedPatient();
            Doctor doctor = getSelectedDoctor();
            
            if (patient == null || doctor == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get date and time from spinners
            LocalDateTime consultationDateTime = getSelectedDateTime();
            
            String consultationType = (String) consultationTypeComboBox.getSelectedItem();
            String symptoms = symptomsField.getText().trim();
            
            if (symptoms.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter symptoms.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Consultation consultation = consultationControl.createConsultation(
                patient, doctor, consultationDateTime, consultationType, symptoms);
            // Start lifecycle: Immediately set to In Progress when starting consultation flow
            consultationControl.updateConsultationStatus(consultation.getConsultationID(), "In Progress");
            
            JOptionPane.showMessageDialog(this, "Consultation created successfully!\nID: " + consultation.getConsultationID(), 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Set queue status to Prescriptioning for this patient and navigate to Prescription panel
            try {
                DoublyLinkedList<QueueEntry> queueList = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
                if (queueList != null) {
                    for (int i = 1; i <= queueList.getSize(); i++) {
                        adt.Node<QueueEntry> node = queueList.getElement(i);
                        if (node != null && node.getEntry().getPatient() != null &&
                            node.getEntry().getPatient().getPatientID().equals(patient.getPatientID())) {
                            node.getEntry().markPrescriptioning();
                            break;
                        }
                    }
                    FileUtils.writeDataToFile("queue", queueList);
                }
            } catch (Exception ignore) {}
            
            clearFields();
            loadConsultations();
            
            // Navigate to Prescription panel and preselect patient/consultation
            PrescriptionPanel pp = mainFrame.getPrescriptionPanel();
            if (pp != null) {
                // best-effort: reload and navigate
                pp.reloadPrescriptionData();
            }
            mainFrame.showPanel("prescriptionPanel");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating consultation: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
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
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this consultation?", 
                                                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Note: In a real implementation, you would add a delete method to ConsultationControl
            JOptionPane.showMessageDialog(this, "Delete functionality not implemented in this demo.", 
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        try {
            // Load shared queue from patient management
            DoublyLinkedList<QueueEntry> queueList = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
            if (queueList == null) queueList = new DoublyLinkedList<>();
            
            QueueEntry currentConsulting = null;
            int consultingIndex = -1;
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && "Consulting".equals(node.getEntry().getStatus())) {
                    currentConsulting = node.getEntry();
                    consultingIndex = i;
                    break;
                }
            }
            
            if (currentConsulting == null) {
                JOptionPane.showMessageDialog(this, "No patient is currently consulting.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mark as done and remove from queue
            currentConsulting.markConsultationDone();
            if (consultingIndex > 0) {
                queueList.deleteAtPosition(consultingIndex);
            }
            
            // Promote next Waiting to Consulting, if any
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && "Waiting".equals(node.getEntry().getStatus())) {
                    node.getEntry().markConsulting();
                    break;
                }
            }
            
            FileUtils.writeDataToFile("queue", queueList);
            
            Patient patient = currentConsulting.getPatient();
            String message = "Consultation completed successfully!\n";
            message += "Patient: " + (patient != null ? patient.getPatientName() : "N/A") + "\n";
            message += "Queue Number: " + currentConsulting.getQueueNumber() + "\n";
            message += "Status: Done";
            
            JOptionPane.showMessageDialog(this, message, "Consultation Completed", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh consultations
            loadConsultations();
            
            // Update current consulting display
            updateCurrentConsultingPatient();
            
            // Ask QueuePanel to refresh if available
            if (queuePanel != null) {
                queuePanel.refreshQueueDisplay();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error completing consultation: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_doneButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.showPanel("consultationManagement");
    }//GEN-LAST:event_backButtonActionPerformed

    private void clearFields() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        
        // Reset date and time spinners to current date/time
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        consultationTypeComboBox.setSelectedIndex(0);
        symptomsField.setText("");
        statusComboBox.setSelectedIndex(0);
    }
    
    private void testDataLoading() {
        System.out.println("=== Testing Data Loading ===");
        System.out.println("Patient list size: " + patientList.getSize());
        System.out.println("Doctor list size: " + doctorList.getSize());
        
        System.out.println("Patient combo box items: " + patientComboBox.getItemCount());
        System.out.println("Doctor combo box items: " + doctorComboBox.getItemCount());
        
        if (patientComboBox.getItemCount() > 0) {
            System.out.println("First patient item: " + patientComboBox.getItemAt(0));
        }
        
        if (doctorComboBox.getItemCount() > 0) {
            System.out.println("First doctor item: " + doctorComboBox.getItemAt(0));
        }
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
        DoublyLinkedList<QueueEntry> queueList = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
        QueueEntry currentConsulting = null;
        if (queueList != null) {
            for (QueueEntry entry : queueList) {
                if ("Consulting".equals(entry.getStatus())) {
                    currentConsulting = entry;
                    break;
                }
            }
        }
        if (currentConsulting != null) {
            Patient patient = currentConsulting.getPatient();
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            jLabel7.setText("Current Consulting: " + patientName + " (Queue: " + currentConsulting.getQueueNumber() + ")");
            jLabel7.setForeground(Color.GREEN); // Indicate current consulting
            doneButton.setEnabled(true); // Enable done button
        } else {
            jLabel7.setText("Current Consulting: None - Please start consultation from queue");
            jLabel7.setForeground(Color.BLACK); // Reset color
            doneButton.setEnabled(false); // Disable when none
        }
    }

    /**
     * Selects a patient in the patient combo box by patient ID.
     */
    public void selectPatientById(String patientId) {
        if (patientId == null || patientComboBox.getItemCount() == 0) return;
        for (int i = 0; i < patientComboBox.getItemCount(); i++) {
            String item = patientComboBox.getItemAt(i);
            if (item != null && item.startsWith(patientId + " ")) {
                patientComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton backButton;
    private javax.swing.JComboBox<String> consultationTypeComboBox;
    private javax.swing.JTable consultationTable;
    private javax.swing.JTextField dateTimeField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JComboBox<String> doctorComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> patientComboBox;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JTextField symptomsField;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton doneButton;
    // End of variables declaration//GEN-END:variables
} 