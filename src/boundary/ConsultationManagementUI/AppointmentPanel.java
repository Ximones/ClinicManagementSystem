package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.Appointment;
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
 * Appointment Management Panel
 * @author Zhen Bang
 */
public class AppointmentPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private JTable appointmentTable;
    private JScrollPane tableScrollPane;
    private JPanel dataPanel;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JComboBox<String> appointmentTypeComboBox;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> consultationComboBox;
    private JTextField reasonField;
    private JTextField notesField;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    
    /**
     * Creates new form AppointmentPanel
     */
    public AppointmentPanel(MainFrame mainFrame) {
        this(mainFrame, new ConsultationControl());
    }
    
    /**
     * Creates new form AppointmentPanel with consultation control
     */
    public AppointmentPanel(MainFrame mainFrame, ConsultationControl consultationControl) {
        this.mainFrame = mainFrame;
        this.consultationControl = consultationControl;
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Initialize the appointment management interface
        initializeAppointmentInterface();
        loadAppointments();
    }

    public void reloadData() {
        loadAppointments();
        populateComboBoxes();
    }
    
    private void initializeAppointmentInterface() {
        // Create the data panel
        dataPanel = new JPanel(new BorderLayout());
        
        // Create the table
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Type", "Status", "Reason"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);
        
        tableScrollPane = new JScrollPane(appointmentTable);
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
        panel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        
        // Initialize components
        patientComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        appointmentTypeComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>();
        consultationComboBox = new JComboBox<>();
        reasonField = new JTextField(20);
        notesField = new JTextField(20);
        
        // Setup date/time spinners
        setupDateTimeSpinners();
        
        // Left column: Patient, Doctor, Date, Type
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
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(appointmentTypeComboBox, gbc);
        
        // Right column: Reason, Original Consultation, Status, Notes
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(reasonField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Original Consultation:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(consultationComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(notesField, gbc);
        
        // Populate combo boxes
        populateComboBoxes();
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Appt");
        JButton updateButton = new JButton("Update Appt");
        JButton deleteButton = new JButton("Delete Appt");
        JButton todayButton = new JButton("Today's Appts");
        JButton upcomingButton = new JButton("Upcoming Appts");
        JButton backButton = new JButton("Back");
        
        addButton.addActionListener(e -> addAppointment());
        updateButton.addActionListener(e -> updateAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        todayButton.addActionListener(e -> showTodayAppointments());
        upcomingButton.addActionListener(e -> showUpcomingAppointments());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(todayButton);
        panel.add(upcomingButton);
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
        appointmentTypeComboBox.removeAllItems();
        statusComboBox.removeAllItems();
        consultationComboBox.removeAllItems();
        
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
        
        // Load and populate consultations
        List<Consultation> consultations = consultationControl.getAllConsultations();
        for (Consultation consultation : consultations) {
            String item = consultation.getConsultationID() + " - " + 
                         (consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "N/A");
            consultationComboBox.addItem(item);
        }
    }
    
    private void loadAppointments() {
        if (appointmentTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);
        
        List<Appointment> appointments = consultationControl.getAllAppointments();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Appointment appointment : appointments) {
            String date = appointment.getAppointmentDateTime() != null ? 
                         appointment.getAppointmentDateTime().format(fmt) : "-";
            
            model.addRow(new Object[]{
                appointment.getAppointmentID(),
                appointment.getPatient() != null ? appointment.getPatient().getPatientName() : "-",
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : "-",
                date,
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReason()
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
    
    private Consultation getSelectedConsultation() {
        String selected = (String) consultationComboBox.getSelectedItem();
        if (selected != null) {
            String consultationID = selected.split(" - ")[0];
            return consultationControl.getConsultation(consultationID);
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
    
    private void addAppointment() {
        try {
            Patient patient = getSelectedPatient();
            Doctor doctor = getSelectedDoctor();
            Consultation consultation = getSelectedConsultation();
            
            if (patient == null || doctor == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDateTime appointmentDateTime = getSelectedDateTime();
            String appointmentType = (String) appointmentTypeComboBox.getSelectedItem();
            String reason = reasonField.getText().trim();
            
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason for the appointment.", "Error", JOptionPane.ERROR_MESSAGE);
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
    }
    
    private void updateAppointment() {
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
    }
    
    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this appointment?", 
                                                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Delete functionality not implemented in this demo.", 
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showTodayAppointments() {
        List<Appointment> todayAppointments = consultationControl.getTodayAppointments();
        
        if (todayAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments scheduled for today.", "Today's Appointments", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("Today's Appointments:\n\n");
            for (Appointment appointment : todayAppointments) {
                message.append("ID: ").append(appointment.getAppointmentID()).append("\n");
                message.append("Patient: ").append(appointment.getPatient() != null ? appointment.getPatient().getPatientName() : "N/A").append("\n");
                message.append("Doctor: ").append(appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A").append("\n");
                message.append("Time: ").append(appointment.getFormattedDateTime()).append("\n");
                message.append("Status: ").append(appointment.getStatus()).append("\n\n");
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Today's Appointments", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showUpcomingAppointments() {
        List<Appointment> upcomingAppointments = consultationControl.getUpcomingAppointments();
        
        if (upcomingAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No upcoming appointments.", "Upcoming Appointments", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("Upcoming Appointments:\n\n");
            for (Appointment appointment : upcomingAppointments) {
                message.append("ID: ").append(appointment.getAppointmentID()).append("\n");
                message.append("Patient: ").append(appointment.getPatient() != null ? appointment.getPatient().getPatientName() : "N/A").append("\n");
                message.append("Doctor: ").append(appointment.getDoctor() != null ? appointment.getDoctor().getName() : "N/A").append("\n");
                message.append("Time: ").append(appointment.getFormattedDateTime()).append("\n");
                message.append("Status: ").append(appointment.getStatus()).append("\n\n");
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Upcoming Appointments", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearFields() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        appointmentTypeComboBox.setSelectedIndex(0);
        reasonField.setText("");
        consultationComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        notesField.setText("");
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
        titleLabel.setText(" Appointment Management");
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
