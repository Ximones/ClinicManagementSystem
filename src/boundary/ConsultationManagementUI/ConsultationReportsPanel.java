package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationControl;
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
    private JTextArea reportTextArea;
    private JScrollPane textScrollPane;
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
        tableScrollPane.setPreferredSize(new Dimension(400, 200));
        
        // Create text area for report preview
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textScrollPane = new JScrollPane(reportTextArea);
        textScrollPane.setPreferredSize(new Dimension(500, 300));
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create split pane for table and text area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, textScrollPane);
        splitPane.setDividerLocation(400);
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(controlPanel, BorderLayout.NORTH);
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Wrap main content in scroll pane
        JScrollPane mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Add scrollable content to data panel
        dataPanel.add(mainScrollPane, BorderLayout.CENTER);
        
        // Add data panel to content panel
        contentPanel.add(dataPanel);
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
        JButton previewButton = new JButton("Preview Report");
        JButton exportButton = new JButton("Export Report");
        JButton clearButton = new JButton("Clear Preview");
        JButton backButton = new JButton("Back");
        
        generateButton.addActionListener(e -> generateReport());
        previewButton.addActionListener(e -> previewReport());
        exportButton.addActionListener(e -> exportReport());
        clearButton.addActionListener(e -> clearPreview());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(generateButton);
        panel.add(previewButton);
        panel.add(exportButton);
        panel.add(clearButton);
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
        reportTypeComboBox.addItem("Queue Statistics Report");
        reportTypeComboBox.addItem("Doctor Performance Report");
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
            
            switch (selectedReport) {
                case "Consultation Statistics Report":
                    reportContent = generateConsultationStatisticsReport(fromDate, toDate);
                    break;
                case "Appointment Summary Report":
                    reportContent = generateAppointmentSummaryReport(fromDate, toDate);
                    break;
                case "Queue Statistics Report":
                    reportContent = generateQueueStatisticsReport();
                    break;
                case "Doctor Performance Report":
                    reportContent = generateDoctorPerformanceReport(fromDate, toDate);
                    break;
                case "Patient Consultation History":
                    reportContent = generatePatientConsultationHistory();
                    break;
                case "Monthly Consultation Summary":
                    reportContent = generateMonthlyConsultationSummary();
                    break;
                default:
                    reportContent = "Report type not implemented.";
            }
            
            // Display report in text area
            reportTextArea.setText(reportContent);
            
            // Add to report table
            addReportToTable(selectedReport, "Generated");
            
            JOptionPane.showMessageDialog(this, "Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void previewReport() {
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();
        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();
        
        if (selectedReport == null) {
            JOptionPane.showMessageDialog(this, "Please select a report type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String reportContent = "";
            
            switch (selectedReport) {
                case "Consultation Statistics Report":
                    reportContent = generateConsultationStatisticsReport(fromDate, toDate);
                    break;
                case "Appointment Summary Report":
                    reportContent = generateAppointmentSummaryReport(fromDate, toDate);
                    break;
                case "Queue Statistics Report":
                    reportContent = generateQueueStatisticsReport();
                    break;
                case "Doctor Performance Report":
                    reportContent = generateDoctorPerformanceReport(fromDate, toDate);
                    break;
                case "Patient Consultation History":
                    reportContent = generatePatientConsultationHistory();
                    break;
                case "Monthly Consultation Summary":
                    reportContent = generateMonthlyConsultationSummary();
                    break;
                default:
                    reportContent = "Report type not implemented.";
            }
            
            // Display report in text area
            reportTextArea.setText(reportContent);
            
            JOptionPane.showMessageDialog(this, "Report preview generated!", "Preview", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating preview: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportReport() {
        if (reportTextArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report to export. Please generate a report first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new java.io.File("consultation_report.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.print(reportTextArea.getText());
                }
                JOptionPane.showMessageDialog(this, "Report exported successfully to " + file.getName(), 
                                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                                            "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearPreview() {
        reportTextArea.setText("");
        JOptionPane.showMessageDialog(this, "Preview cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
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
    
    private String generateQueueStatisticsReport() {
        StringBuilder report = new StringBuilder();
        report.append("QUEUE STATISTICS REPORT\n");
        report.append("=======================\n\n");
        
        List<QueueEntry> queueEntries = consultationControl.getAllQueueEntries();
        
        int totalPatients = queueEntries.size();
        int waitingPatients = 0;
        int consultingPatients = 0;
        int completedPatients = 0;
        int prescribingPatients = 0;
        long totalWaitingTime = 0;
        
        for (QueueEntry entry : queueEntries) {
            switch (entry.getStatus()) {
                case "Waiting":
                    waitingPatients++;
                    break;
                case "Consulting":
                    consultingPatients++;
                    break;
                case "Done":
                    completedPatients++;
                    break;
                case "Prescribing":
                    prescribingPatients++;
                    break;
            }
            totalWaitingTime += entry.getWaitingTimeMillis();
        }
        
        report.append("Total Patients in Queue: ").append(totalPatients).append("\n");
        report.append("Currently Waiting: ").append(waitingPatients).append("\n");
        report.append("Currently Consulting: ").append(consultingPatients).append("\n");
        report.append("Currently Prescribing: ").append(prescribingPatients).append("\n");
        report.append("Completed: ").append(completedPatients).append("\n\n");
        
        if (totalPatients > 0) {
            long averageWaitingTime = totalWaitingTime / totalPatients;
            long minutes = averageWaitingTime / (1000 * 60);
            long seconds = (averageWaitingTime % (1000 * 60)) / 1000;
            report.append("Average Waiting Time: ").append(minutes).append("m ").append(seconds).append("s\n");
        }
        
        return report.toString();
    }
    
    private String generateDoctorPerformanceReport(Date fromDate, Date toDate) {
        StringBuilder report = new StringBuilder();
        report.append("DOCTOR PERFORMANCE REPORT\n");
        report.append("==========================\n\n");
        
        LocalDateTime fromDateTime = fromDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toDateTime = toDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        
        List<Consultation> consultations = consultationControl.getAllConsultations();
        
        // Count consultations by doctor
        java.util.Map<String, Integer> doctorConsultations = new java.util.HashMap<>();
        java.util.Map<String, Integer> doctorCompleted = new java.util.HashMap<>();
        
        for (Consultation consultation : consultations) {
            if (consultation.getConsultationDateTime() != null &&
                !consultation.getConsultationDateTime().isBefore(fromDateTime) &&
                !consultation.getConsultationDateTime().isAfter(toDateTime) &&
                consultation.getDoctor() != null) {
                
                String doctorName = consultation.getDoctor().getName();
                doctorConsultations.put(doctorName, doctorConsultations.getOrDefault(doctorName, 0) + 1);
                
                if ("Completed".equals(consultation.getStatus())) {
                    doctorCompleted.put(doctorName, doctorCompleted.getOrDefault(doctorName, 0) + 1);
                }
            }
        }
        
        report.append("Date Range: ").append(fromDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        report.append(" to ").append(toDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        for (String doctorName : doctorConsultations.keySet()) {
            int total = doctorConsultations.get(doctorName);
            int completed = doctorCompleted.getOrDefault(doctorName, 0);
            double completionRate = total > 0 ? (double) completed / total * 100 : 0;
            
            report.append("Doctor: ").append(doctorName).append("\n");
            report.append("  Total Consultations: ").append(total).append("\n");
            report.append("  Completed: ").append(completed).append("\n");
            report.append("  Completion Rate: ").append(String.format("%.1f%%", completionRate)).append("\n\n");
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
