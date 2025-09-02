package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.Consultation;
import enitity.Appointment;
import enitity.QueueEntry;
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
import utility.ReportGenerator;

/**
 * Consultation Reports Panel
 * @author Zhen Bang
 */
public class ConsultationReportsPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private JTable reportTable;
    private JScrollPane tableScrollPane;
    private JPanel dataPanel;
    private JComboBox<String> reportTypeComboBox;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    
    /**
     * Creates new form ConsultationReportsPanel
     */
    public ConsultationReportsPanel(MainFrame mainFrame) {
        this(mainFrame, new ConsultationControl());
    }
    
    /**
     * Creates new form ConsultationReportsPanel with consultation control
     */
    public ConsultationReportsPanel(MainFrame mainFrame, ConsultationControl consultationControl) {
        this.mainFrame = mainFrame;
        this.consultationControl = consultationControl;
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Initialize the reports interface
        initializeReportsInterface();
    }

    public void reloadData() {
        // Refresh reports data if needed
    }
    
    private void initializeReportsInterface() {
        // Create the data panel
        dataPanel = new JPanel(new BorderLayout());
        
        // Create the table
        String[] columnNames = {"Report Type", "Generated Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.getTableHeader().setReorderingAllowed(false);
        
        tableScrollPane = new JScrollPane(reportTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(controlPanel, BorderLayout.NORTH);
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
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Initialize components
        reportTypeComboBox = new JComboBox<>();
        setupDateSpinners();
        
        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        panel.add(reportTypeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("From Date:"), gbc);
        gbc.gridx = 1;
        panel.add(fromDateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("To Date:"), gbc);
        gbc.gridx = 1;
        panel.add(toDateSpinner, gbc);
        
        // Populate report types
        populateReportTypes();
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton generateButton = new JButton("Generate Report");
        JButton backButton = new JButton("Back");
        
        generateButton.addActionListener(e -> generateReport());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(generateButton);
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
        
        // Set default dates (current month)
        Calendar calendar = Calendar.getInstance();
        toDateSpinner.setValue(calendar.getTime());
        
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        fromDateSpinner.setValue(calendar.getTime());
        
        // Style the spinners
        fromDateSpinner.setPreferredSize(new Dimension(120, 25));
        toDateSpinner.setPreferredSize(new Dimension(120, 25));
    }
    
    private void populateReportTypes() {
        reportTypeComboBox.removeAllItems();
        
        reportTypeComboBox.addItem("Consultation Statistics Report");
        reportTypeComboBox.addItem("Appointment Summary Report");
        reportTypeComboBox.addItem("Patient Consultation History");
        reportTypeComboBox.addItem("Monthly Consultation Summary");
    }
    
    private void generateReport() {
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();
        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();
        
        if (selectedReport == null) {
            JOptionPane.showMessageDialog(this, "Please select a report type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String reportContent = "";
            LocalDateTime fromDateTime = fromDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime toDateTime = toDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            
            switch (selectedReport) {
                case "Consultation Statistics Report":
                    reportContent = generateConsultationStatisticsReport(fromDate, toDate);
                    // Generate PDF
                    ReportGenerator.generateConsultationStatisticsPDF(consultationControl.getConsultationList(), fromDateTime, toDateTime);
                    break;
                case "Appointment Summary Report":
                    reportContent = generateAppointmentSummaryReport(fromDate, toDate);
                    // Generate PDF
                    ReportGenerator.generateAppointmentSummaryPDF(consultationControl.getAppointmentList(), fromDateTime, toDateTime);
                    break;
                case "Patient Consultation History":
                    reportContent = generatePatientConsultationHistory();
                    // This is a text-based report, no PDF needed
                    break;
                case "Monthly Consultation Summary":
                    reportContent = generateMonthlyConsultationSummary();
                    // Generate PDF
                    ReportGenerator.generateMonthlyConsultationPDF(consultationControl.getConsultationList());
                    break;
                default:
                    reportContent = "Report type not implemented.";
            }
            
            // Add to report table
            addReportToTable(selectedReport, "Generated");
            
            // Show success message with PDF info
            String message = "Report generated successfully!";
            if (!selectedReport.equals("Patient Consultation History")) {
                message += "\nPDF file has been created in the project directory.";
            }
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    



    
    private void addReportToTable(String reportType, String status) {
        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentTime = LocalDateTime.now().format(fmt);
        
        model.addRow(new Object[]{reportType, currentTime, status});
    }
    
    private String generateConsultationStatisticsReport(Date fromDate, Date toDate) {
        StringBuilder report = new StringBuilder();
        report.append("CONSULTATION STATISTICS REPORT\n");
        report.append("==============================\n\n");
        
        LocalDateTime fromDateTime = fromDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toDateTime = toDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        
        List<Consultation> consultations = consultationControl.getAllConsultations();
        
        int totalConsultations = 0;
        int completedConsultations = 0;
        int scheduledConsultations = 0;
        int cancelledConsultations = 0;
        
        for (Consultation consultation : consultations) {
            if (consultation.getConsultationDateTime() != null &&
                !consultation.getConsultationDateTime().isBefore(fromDateTime) &&
                !consultation.getConsultationDateTime().isAfter(toDateTime)) {
                
                totalConsultations++;
                
                switch (consultation.getStatus()) {
                    case "Completed":
                        completedConsultations++;
                        break;
                    case "Scheduled":
                        scheduledConsultations++;
                        break;
                    case "Cancelled":
                        cancelledConsultations++;
                        break;
                }
            }
        }
        
        report.append("Date Range: ").append(fromDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        report.append(" to ").append(toDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        report.append("Total Consultations: ").append(totalConsultations).append("\n");
        report.append("Completed: ").append(completedConsultations).append("\n");
        report.append("Scheduled: ").append(scheduledConsultations).append("\n");
        report.append("Cancelled: ").append(cancelledConsultations).append("\n\n");
        
        if (totalConsultations > 0) {
            double completionRate = (double) completedConsultations / totalConsultations * 100;
            report.append("Completion Rate: ").append(String.format("%.1f%%", completionRate)).append("\n");
        }
        
        return report.toString();
    }
    
    private String generateAppointmentSummaryReport(Date fromDate, Date toDate) {
        StringBuilder report = new StringBuilder();
        report.append("APPOINTMENT SUMMARY REPORT\n");
        report.append("===========================\n\n");
        
        LocalDateTime fromDateTime = fromDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toDateTime = toDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        
        List<Appointment> appointments = consultationControl.getAllAppointments();
        
        int totalAppointments = 0;
        int confirmedAppointments = 0;
        int completedAppointments = 0;
        int cancelledAppointments = 0;
        int noShowAppointments = 0;
        
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDateTime() != null &&
                !appointment.getAppointmentDateTime().isBefore(fromDateTime) &&
                !appointment.getAppointmentDateTime().isAfter(toDateTime)) {
                
                totalAppointments++;
                
                switch (appointment.getStatus()) {
                    case "Confirmed":
                        confirmedAppointments++;
                        break;
                    case "Completed":
                        completedAppointments++;
                        break;
                    case "Cancelled":
                        cancelledAppointments++;
                        break;
                    case "No-show":
                        noShowAppointments++;
                        break;
                }
            }
        }
        
        report.append("Date Range: ").append(fromDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        report.append(" to ").append(toDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        report.append("Total Appointments: ").append(totalAppointments).append("\n");
        report.append("Confirmed: ").append(confirmedAppointments).append("\n");
        report.append("Completed: ").append(completedAppointments).append("\n");
        report.append("Cancelled: ").append(cancelledAppointments).append("\n");
        report.append("No-show: ").append(noShowAppointments).append("\n\n");
        
        if (totalAppointments > 0) {
            double attendanceRate = (double) completedAppointments / totalAppointments * 100;
            report.append("Attendance Rate: ").append(String.format("%.1f%%", attendanceRate)).append("\n");
        }
        
        return report.toString();
    }
    

    

    
    private String generatePatientConsultationHistory() {
        StringBuilder report = new StringBuilder();
        report.append("PATIENT CONSULTATION HISTORY\n");
        report.append("============================\n\n");
        
        List<Consultation> consultations = consultationControl.getAllConsultations();
        
        // Group consultations by patient
        java.util.Map<String, java.util.List<Consultation>> patientConsultations = new java.util.HashMap<>();
        
        for (Consultation consultation : consultations) {
            if (consultation.getPatient() != null) {
                String patientName = consultation.getPatient().getPatientName();
                patientConsultations.computeIfAbsent(patientName, k -> new java.util.ArrayList<>()).add(consultation);
            }
        }
        
        for (String patientName : patientConsultations.keySet()) {
            report.append("Patient: ").append(patientName).append("\n");
            report.append("  Consultations:\n");
            
            for (Consultation consultation : patientConsultations.get(patientName)) {
                String date = consultation.getConsultationDateTime() != null ? 
                             consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
                
                report.append("    - ").append(date).append(" (").append(consultation.getConsultationType()).append(") - ").append(consultation.getStatus()).append("\n");
            }
            report.append("\n");
        }
        
        return report.toString();
    }
    
    private String generateMonthlyConsultationSummary() {
        StringBuilder report = new StringBuilder();
        report.append("MONTHLY CONSULTATION SUMMARY\n");
        report.append("============================\n\n");
        
        List<Consultation> consultations = consultationControl.getAllConsultations();
        
        // Group consultations by month
        java.util.Map<String, Integer> monthlyConsultations = new java.util.HashMap<>();
        
        for (Consultation consultation : consultations) {
            if (consultation.getConsultationDateTime() != null) {
                String monthYear = consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                monthlyConsultations.put(monthYear, monthlyConsultations.getOrDefault(monthYear, 0) + 1);
            }
        }
        
        for (String monthYear : monthlyConsultations.keySet()) {
            report.append(monthYear).append(": ").append(monthlyConsultations.get(monthYear)).append(" consultations\n");
        }
        
        return report.toString();
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
        titleLabel.setText(" Consultation Reports");
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
