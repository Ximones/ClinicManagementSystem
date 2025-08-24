/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationControl;
import enitity.Appointment;
import enitity.Consultation;
import enitity.Patient;
import enitity.Doctor;
import adt.DoublyLinkedList;
import adt.Pair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import utility.FileUtils;

/**
 * Appointment Management Panel
 * @author Zhen Bang
 */
public class AppointmentPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private DefaultTableModel appointmentTableModel;
    private DoublyLinkedList<Pair<String, Patient>> patientList;
    private DoublyLinkedList<Pair<String, Doctor>> doctorList;
    private DoublyLinkedList<Pair<String, Consultation>> consultationList;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    
    /**
     * Creates new form AppointmentPanel
     */
    public AppointmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.consultationControl = new ConsultationControl();
        
        // Initialize spinners before layout setup
        setupDateTimeSpinners();
        
        initComponents();
        setupUI();
        initializeData();
        loadAppointments();
    }
    
    private void setupUI() {
        setPreferredSize(new Dimension(700, 500));
        setBackground(new Color(240, 248, 255));
        
        // Style title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        
        // Style buttons
        styleButton(addButton, "Add Appointment", new Color(70, 130, 180));
        styleButton(updateButton, "Update Appointment", new Color(60, 179, 113));
        styleButton(deleteButton, "Delete Appointment", new Color(220, 20, 60));
        styleButton(backButton, "Back", new Color(128, 128, 128));
        
        // Add a refresh button for debugging
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> {
            System.out.println("=== Refreshing Appointment Data ===");
            initializeData();
            loadAppointments();
            System.out.println("After refresh - Patient combo box items: " + patientComboBox.getItemCount());
            System.out.println("After refresh - Doctor combo box items: " + doctorComboBox.getItemCount());
            System.out.println("After refresh - Consultation combo box items: " + consultationComboBox.getItemCount());
        });
        styleButton(refreshButton, "Refresh Data", new Color(255, 165, 0));
        
        // Setup table
        setupTable();
    }
    
    private void styleButton(JButton button, String text, Color backgroundColor) {
        button.setText(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupTable() {
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Reason"};
        appointmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable.setModel(appointmentTableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void initializeData() {
        // Load real patient data from file
        loadPatientData();
        
        // Load real doctor data from file
        loadDoctorData();
        
        // Load real consultation data from file
        loadConsultationData();
        
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
    
    private void loadConsultationData() {
        consultationList = new DoublyLinkedList<>();
        
        // Load consultations from file (if they exist)
        DoublyLinkedList<Pair<String, Consultation>> loadedConsultations = (DoublyLinkedList<Pair<String, Consultation>>) FileUtils.readDataFromFile("consultations");
        
        if (loadedConsultations != null && !loadedConsultations.isEmpty()) {
            System.out.println("Loading " + loadedConsultations.getSize() + " consultations from file");
            
            for (Pair<String, Consultation> consultationPair : loadedConsultations) {
                consultationList.insertLast(consultationPair);
                System.out.println("Loaded consultation: " + consultationPair.getKey());
            }
        } else {
            System.out.println("No consultation data found in file. Using empty list.");
        }
    }
    
    private void populateComboBoxes() {
        patientComboBox.removeAllItems();
        doctorComboBox.removeAllItems();
        consultationComboBox.removeAllItems();
        appointmentTypeComboBox.removeAllItems();
        statusComboBox.removeAllItems();
        
        // Add patients
        for (Pair<String, Patient> pair : patientList) {
            Patient patient = pair.getValue();
            patientComboBox.addItem(patient.getPatientID() + " - " + patient.getPatientName());
        }
        
        // Add doctors
        for (Pair<String, Doctor> pair : doctorList) {
            Doctor doctor = pair.getValue();
            doctorComboBox.addItem(doctor.getDoctorID() + " - " + doctor.getName());
        }
        
        // Add consultations
        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            consultationComboBox.addItem(consultation.getConsultationID() + " - " + 
                                       (consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "N/A"));
        }
        
        // Add appointment types
        appointmentTypeComboBox.addItem("Follow-up");
        appointmentTypeComboBox.addItem("Review");
        appointmentTypeComboBox.addItem("Check-up");
        
        // Add statuses
        statusComboBox.addItem("Scheduled");
        statusComboBox.addItem("Confirmed");
        statusComboBox.addItem("Completed");
        statusComboBox.addItem("Cancelled");
        statusComboBox.addItem("No-show");
    }
    
    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        List<Appointment> appointments = consultationControl.getAllAppointments();
        
        for (Appointment appointment : appointments) {
            Object[] row = {
                appointment.getAppointmentID(),
                appointment.getPatient() != null ? appointment.getPatient().getPatientName() : "N/A",
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A",
                appointment.getFormattedDateTime(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReason()
            };
            appointmentTableModel.addRow(row);
        }
    }
    
    private Patient getSelectedPatient() {
        String selected = (String) patientComboBox.getSelectedItem();
        if (selected != null) {
            String patientID = selected.split(" - ")[0];
            Object result = patientList.findByKey(patientID);
            if (result instanceof Patient) {
                return (Patient) result;
            }
        }
        return null;
    }
    
    private Doctor getSelectedDoctor() {
        String selected = (String) doctorComboBox.getSelectedItem();
        if (selected != null) {
            String doctorID = selected.split(" - ")[0];
            Object result = doctorList.findByKey(doctorID);
            if (result instanceof Doctor) {
                return (Doctor) result;
            }
        }
        return null;
    }
    
    private Consultation getSelectedConsultation() {
        String selected = (String) consultationComboBox.getSelectedItem();
        if (selected != null) {
            String consultationID = selected.split(" - ")[0];
            Object result = consultationList.findByKey(consultationID);
            if (result instanceof Consultation) {
                return (Consultation) result;
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

        titleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        appointmentTable = new javax.swing.JTable();
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
        appointmentTypeComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        reasonField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        consultationComboBox = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(700, 500));

        titleLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Appointment Management");

        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Reason"
            }
        ));
        jScrollPane1.setViewportView(appointmentTable);

        addButton.setText("Add Appointment");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Update Appointment");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete Appointment");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Appointment Details"));

        jLabel1.setText("Patient:");

        jLabel2.setText("Doctor:");

        jLabel3.setText("Date/Time (dd/MM/yyyy HH:mm):");

        jLabel4.setText("Type:");

        jLabel5.setText("Reason:");

        jLabel6.setText("Status:");

        jLabel7.setText("Original Consultation:");

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
                    .addComponent(dateTimeField)
                    .addComponent(appointmentTypeComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(reasonField)
                    .addComponent(statusComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(consultationComboBox, 0, 200, Short.MAX_VALUE))
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
                    .addComponent(dateTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(appointmentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(reasonField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(consultationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            Patient patient = getSelectedPatient();
            Doctor doctor = getSelectedDoctor();
            Consultation consultation = getSelectedConsultation();
            
            if (patient == null || doctor == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get date and time from spinners
            LocalDateTime appointmentDateTime = getSelectedDateTime();
            
            String appointmentType = (String) appointmentTypeComboBox.getSelectedItem();
            String reason = reasonField.getText().trim();
            
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter reason for appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Appointment appointment = consultationControl.createAppointment(
                patient, doctor, appointmentDateTime, appointmentType, reason, consultation);
            
            JOptionPane.showMessageDialog(this, "Appointment created successfully!\nID: " + appointment.getAppointmentID(), 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearFields();
            loadAppointments();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating appointment: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String appointmentID = (String) appointmentTable.getValueAt(selectedRow, 0);
        String newStatus = (String) statusComboBox.getSelectedItem();
        
        boolean updated = consultationControl.updateAppointmentStatus(appointmentID, newStatus);
        
        if (updated) {
            JOptionPane.showMessageDialog(this, "Appointment status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this appointment?", 
                                                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Note: In a real implementation, you would add a delete method to ConsultationControl
            JOptionPane.showMessageDialog(this, "Delete functionality not implemented in this demo.", 
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

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
        
        appointmentTypeComboBox.setSelectedIndex(0);
        reasonField.setText("");
        statusComboBox.setSelectedIndex(0);
        consultationComboBox.setSelectedIndex(0);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox<String> appointmentTypeComboBox;
    private javax.swing.JTable appointmentTable;
    private javax.swing.JButton backButton;
    private javax.swing.JComboBox<String> consultationComboBox;
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
    private javax.swing.JTextField reasonField;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
} 