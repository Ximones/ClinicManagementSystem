package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.QueueEntry;
import enitity.Patient;
import enitity.Consultation;
import enitity.Prescription;
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
import adt.DoublyLinkedList;
import adt.Pair;

/**
 * Panel for managing patient queue in consultation management
 *
 * @author Zhen Bang
 */
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
        DoublyLinkedList<Pair<String, QueueEntry>> entries = consultationControl.getAllQueueEntries();
        for (Pair<String, QueueEntry> pair : entries) {
            QueueEntry e = pair.getValue();
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
            DoublyLinkedList<Pair<String, QueueEntry>> entries = consultationControl.getAllQueueEntries();
            boolean hasWaitingPatients = false;
            for (Pair<String, QueueEntry> pair : entries) {
                QueueEntry entry = pair.getValue();
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
        DoublyLinkedList<Pair<String, QueueEntry>> entries = consultationControl.getAllQueueEntries();
        QueueEntry nextPatient = null;
        
        for (Pair<String, QueueEntry> pair : entries) {
            QueueEntry entry = pair.getValue();
            if ("Waiting".equals(entry.getStatus())) {
                nextPatient = entry;
                break;
            }
        }
        
        if (nextPatient == null) {
            JOptionPane.showMessageDialog(this, "No patients waiting in queue.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check for undispensed prescriptions and remove them
        try {
            removeUndispensedPrescriptions(nextPatient.getPatient());
        } catch (RuntimeException e) {
            // User cancelled the consultation
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
                DoublyLinkedList<Pair<String, Consultation>> list = consultationControl.getConsultationsByPatient(nextPatient.getPatient().getPatientID());
                if (!list.isEmpty()) {
                    Consultation latest = list.getLast().getEntry().getValue();
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
    
    /**
     * Removes undispensed prescriptions for a patient when starting a new consultation
     * @param patient The patient whose prescriptions should be checked
     */
    private void removeUndispensedPrescriptions(Patient patient) {
        if (patient == null) return;
        
        // Get all prescriptions for this patient
        DoublyLinkedList<Pair<String, Prescription>> patientPrescriptions = consultationControl.getPrescriptionsByPatient(patient.getPatientID());
        
        if (patientPrescriptions.isEmpty()) {
            return; // No prescriptions to check
        }
        
        // Check if any prescriptions are undispensed (status is not "Dispensed")
        java.util.List<enitity.Prescription> undispensedPrescriptions = new java.util.ArrayList<>();
        for (Pair<String, Prescription> pair : patientPrescriptions) {
            Prescription prescription = pair.getValue();
            if (!"Dispensed".equals(prescription.getStatus())) {
                undispensedPrescriptions.add(prescription);
            }
        }
        
        if (!undispensedPrescriptions.isEmpty()) {
            // Show confirmation dialog
            StringBuilder message = new StringBuilder();
            message.append("Found ").append(undispensedPrescriptions.size()).append(" undispensed prescription(s) for ").append(patient.getPatientName()).append(":\n\n");
            
            for (enitity.Prescription prescription : undispensedPrescriptions) {
                message.append("• Prescription ID: ").append(prescription.getPrescriptionID()).append("\n");
                message.append("  Diagnosis: ").append(prescription.getDiagnosis()).append("\n");
                message.append("  Status: ").append(prescription.getStatus()).append("\n\n");
            }
            
            message.append("These prescriptions will be removed as medicine was not dispensed.\n");
            message.append("Do you want to continue with the new consultation?");
            
            int choice = JOptionPane.showConfirmDialog(this, 
                message.toString(), 
                "Remove Undispensed Prescriptions", JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                // Remove the undispensed prescriptions
                for (enitity.Prescription prescription : undispensedPrescriptions) {
                    consultationControl.deletePrescription(prescription.getPrescriptionID());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Removed " + undispensedPrescriptions.size() + " undispensed prescription(s).\n" +
                    "Proceeding with new consultation.", 
                    "Prescriptions Removed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // User cancelled - don't start consultation
                throw new RuntimeException("Consultation cancelled by user");
            }
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
