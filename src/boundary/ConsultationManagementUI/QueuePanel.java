package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.QueueEntry;
import enitity.Patient;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import utility.ImageUtils;

public class QueuePanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationPanel consultationPanel;
    private ConsultationControl consultationControl;
    private JTable queueTable;
    private JButton startConsultationButton;
    private JButton completeConsultationButton;

    public QueuePanel(MainFrame mainFrame, ConsultationControl consultationControl) {
        this.mainFrame = mainFrame;
        this.consultationControl = consultationControl;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        buildTable();
        buildButtons();
        buildLayout();
        refreshQueueDisplay();
    }

    public void setConsultationPanel(ConsultationPanel consultationPanel) {
        this.consultationPanel = consultationPanel;
    }

    /**
     * Reloads the queue data and refreshes the display
     */
    public void reloadData() {
        refreshQueueDisplay();
    }

    public void refreshQueueDisplay() {
        if (queueTable == null || consultationControl == null) return;
        // Ensure we see updates made by PatientManagement queue panel
        consultationControl.reloadQueue();
        DefaultTableModel model = (DefaultTableModel) queueTable.getModel();
        model.setRowCount(0);
        List<QueueEntry> entries = consultationControl.getAllQueueEntries();
        for (QueueEntry e : entries) {
            model.addRow(new Object[]{
                e.getQueueNumber(), 
                e.getPatient() != null ? e.getPatient().getPatientName() : "-", 
                e.getStatus(),
                e.getFormattedWaitingTime()
            });
        }
        
        // Update button states based on queue status
        updateButtonStates();
    }

    private void buildTable() {
        queueTable = new JTable(new DefaultTableModel(new Object[][]{}, 
            new String[]{"Queue No", "Patient", "Status", "Waiting Time"}) {
            public boolean isCellEditable(int row, int column) { return false; }
        });
        queueTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void buildButtons() {
        startConsultationButton = new JButton("Start Consultation");
        completeConsultationButton = new JButton("Complete Consultation");
        
        startConsultationButton.addActionListener(e -> startConsultation());
        completeConsultationButton.addActionListener(e -> completeConsultation());
        
        updateButtonStates();
    }
    
    private void buildLayout() {
        // Create table scroll pane
        JScrollPane tableScrollPane = new JScrollPane(queueTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Setup back button
        backButton.setText("Back");
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startConsultationButton);
        buttonPanel.add(completeConsultationButton);
        buttonPanel.add(backButton);
        
        // Add components to content panel
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateButtonStates() {
        // Check if there's a patient currently consulting
        QueueEntry currentConsulting = consultationControl.getCurrentConsultingPatient();
        
        if (currentConsulting != null) {
            // There's a patient consulting - enable complete button, disable start button
            startConsultationButton.setEnabled(false);
            completeConsultationButton.setEnabled(true);
        } else {
            // No patient consulting - check if there are waiting patients
            List<QueueEntry> entries = consultationControl.getAllQueueEntries();
            boolean hasWaitingPatients = false;
            for (QueueEntry entry : entries) {
                if ("Waiting".equals(entry.getStatus())) {
                    hasWaitingPatients = true;
                    break;
                }
            }
            
            startConsultationButton.setEnabled(hasWaitingPatients);
            completeConsultationButton.setEnabled(false);
        }
    }
    
    private void startConsultation() {
        // Get the first waiting patient
        List<QueueEntry> entries = consultationControl.getAllQueueEntries();
        QueueEntry nextPatient = null;
        
        for (QueueEntry entry : entries) {
            if ("Waiting".equals(entry.getStatus())) {
                nextPatient = entry;
                break;
            }
        }
        
        if (nextPatient == null) {
            JOptionPane.showMessageDialog(this, "No patients waiting in queue.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Mark patient as consulting
        boolean success = consultationControl.markPatientConsulting(nextPatient.getQueueNumber());
        
        if (success) {
            // Open consultation panel with the selected patient
            if (consultationPanel != null) {
                consultationPanel.startConsultationForPatient(nextPatient.getPatient());
                mainFrame.showPanel("consultationPanel");
            }
            
            refreshQueueDisplay();
            // Also mark latest consultation for this patient as In Progress
            try {
                java.util.List<enitity.Consultation> list = consultationControl.getConsultationsByPatient(nextPatient.getPatient().getPatientID());
                if (!list.isEmpty()) {
                    enitity.Consultation latest = list.get(list.size() - 1);
                    consultationControl.updateConsultationStatus(latest.getConsultationID(), "In Progress");
                }
            } catch (Exception ignore) {}

            JOptionPane.showMessageDialog(this,
                "Started consultation for " + nextPatient.getPatient().getPatientName(),
                "Consultation Started", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to start consultation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completeConsultation() {
        QueueEntry currentConsulting = consultationControl.getCurrentConsultingPatient();
        
        if (currentConsulting == null) {
            JOptionPane.showMessageDialog(this, "No patient currently consulting.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Complete consultation for " + currentConsulting.getPatient().getPatientName() + "?\n" +
            "This will move the patient to prescription phase.", 
            "Complete Consultation", JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            // Mark patient as prescribing
            currentConsulting.markPrescriptioning();
            consultationControl.saveData();
            
            // Move to prescription panel
            mainFrame.showPanel("prescriptionPanel");
            
            refreshQueueDisplay();
            JOptionPane.showMessageDialog(this, 
                "Consultation completed. Patient moved to prescribing phase.", 
                "Consultation Completed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Consultation Queue");
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.BorderLayout());

        titlePanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("consultationManagement");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
} 
