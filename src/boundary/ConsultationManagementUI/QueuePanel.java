/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.ConsultationManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import boundary.ConsultationManagementUI.ConsultationPanel;
import control.ConsultationControl;
import enitity.Patient;
import enitity.QueueEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import utility.FileUtils;
import utility.ImageUtils;

/**
 * Queue Management Panel for Consultation Module
 * @author Zhen Bang
 */
public class QueuePanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    // private ConsultationControl consultationControl; // no longer the source of truth for queue
    private DoublyLinkedList<Pair<String, Patient>> patientList;
    private DoublyLinkedList<QueueEntry> queueList;
    private DefaultTableModel queueTableModel;
    private Timer waitingTimeTimer;
    private int lastQueueNumber = 1000;
    private ConsultationPanel consultationPanel; // Reference to consultation panel

    /**
     * Creates new form QueuePanel
     */
    public QueuePanel(MainFrame mainFrame) {
        this(mainFrame, null);
    }
    
    /**
     * Creates new form QueuePanel with consultation panel reference
     */
    public QueuePanel(MainFrame mainFrame, ConsultationPanel consultationPanel) {
        this.mainFrame = mainFrame;
        this.consultationPanel = consultationPanel;
        // this.consultationControl = new ConsultationControl();
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        initializeData();
        loadQueueData();
        startWaitingTimeTimer();
    }

    /**
     * Refreshes the queue display - can be called when returning from other panels
     */
    public void refreshQueueDisplay() {
        loadQueueData();
        refreshQueueTable();
        updateStatistics();
    }
    
    /**
     * Sets the consultation panel reference
     */
    public void setConsultationPanel(ConsultationPanel consultationPanel) {
        this.consultationPanel = consultationPanel;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Ensure the queue reflects latest Patient Management queue when shown
        refreshQueueDisplay();
    }

    
    
    private void setupTable() {
        String[] columnNames = {"Queue No.", "Patient ID", "Patient Name", "Status", "Waiting Time", "Queue Position"};
        queueTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queueTable.setModel(queueTableModel);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queueTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void initializeData() {
        // Load patients only if needed for display of names via patientList
        loadPatientData();
        
        // Load queue data from shared patient queue file
        loadQueueData();
        
        // Populate combo boxes (kept for compatibility, but not used to add here)
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
    
    private void loadQueueData() {
        // Use shared queue from patient management module
        DoublyLinkedList<QueueEntry> sharedQueue = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
        if (sharedQueue != null) {
            queueList = sharedQueue;
        } else {
            queueList = new DoublyLinkedList<>();
        }
    }
    
    private void saveQueueData() {
        FileUtils.writeDataToFile("queue", queueList);
    }
    
    private void populateComboBoxes() {
        patientComboBox.removeAllItems();
        
        // Add patients (not used for adding to queue here)
        System.out.println("Populating patients...");
        for (Pair<String, Patient> pair : patientList) {
            Patient patient = pair.getValue();
            String item = patient.getPatientID() + " - " + patient.getPatientName();
            patientComboBox.addItem(item);
            System.out.println("Added patient: " + item);
        }
        
        // Set default selection if available
        if (patientComboBox.getItemCount() > 0) patientComboBox.setSelectedIndex(0);
    }
    
    private void refreshQueueTable() {
        queueTableModel.setRowCount(0);
        
        if (queueList == null || queueList.isEmpty()) {
            return;
        }
        
        int position = 1;
        for (QueueEntry entry : queueList) {
            Patient patient = entry.getPatient();
            String waitingTime = entry.getFormattedWaitingTime();
            Object[] row = {
                entry.getQueueNumber(),
                patient != null ? patient.getPatientID() : "N/A",
                patient != null ? patient.getPatientName() : "N/A",
                entry.getStatus(),
                waitingTime,
                position
            };
            queueTableModel.addRow(row);
            position++;
        }
    }
    
    private String calculateWaitingTime(LocalDateTime enqueueTime) {
        if (enqueueTime == null) {
            return "N/A";
        }
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(enqueueTime, now);
        
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
    
    private void startWaitingTimeTimer() {
        // Update waiting times every 30 seconds
        waitingTimeTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshQueueTable();
            }
        });
        waitingTimeTimer.start();
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
    
    private String generateQueueNumber() {
        lastQueueNumber++;
        return "E" + lastQueueNumber;
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        patientComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        queueTable = new javax.swing.JTable();
        addToQueueButton = new javax.swing.JButton();
        nextPatientButton = new javax.swing.JButton();
        removeFromQueueButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        totalPatientsLabel = new javax.swing.JLabel();
        averageWaitingTimeLabel = new javax.swing.JLabel();
        currentQueueNumberLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 600));

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());
        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Patient Queue Management");
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        queueTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Queue No.", "Patient ID", "Patient Name", "Status", "Waiting Time", "Queue Position"
            }
        ));
        jScrollPane1.setViewportView(queueTable);

        addToQueueButton.setText("Add to Queue");
        addToQueueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToQueueButtonActionPerformed(evt);
            }
        });

        nextPatientButton.setText("Next Patient");
        nextPatientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPatientButtonActionPerformed(evt);
            }
        });

        startConsultationButton = new javax.swing.JButton();
        startConsultationButton.setText("Start Consultation");
        startConsultationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startConsultationButtonActionPerformed(evt);
            }
        });

        removeFromQueueButton.setText("Remove from Queue");
        removeFromQueueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromQueueButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Patient to Queue"));

        jLabel1.setText("Select Patient:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(patientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(patientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Queue Statistics"));

        jLabel2.setText("Total Patients in Queue:");

        jLabel3.setText("Average Waiting Time:");

        jLabel4.setText("Current Queue Number:");

        totalPatientsLabel.setText("0");

        averageWaitingTimeLabel.setText("0m");

        currentQueueNumberLabel.setText("E1000");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalPatientsLabel)
                    .addComponent(averageWaitingTimeLabel)
                    .addComponent(currentQueueNumberLabel))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(totalPatientsLabel))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(averageWaitingTimeLabel))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(currentQueueNumberLabel))
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addToQueueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(nextPatientButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(startConsultationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(removeFromQueueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToQueueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextPatientButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startConsultationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeFromQueueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        titlePanel.add(contentPanel, java.awt.BorderLayout.CENTER);
        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addToQueueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToQueueButtonActionPerformed
        // Redirect to Patient Management's Queue screen for adding
        mainFrame.showPanel("queueManagement");
    }//GEN-LAST:event_addToQueueButtonActionPerformed

    private void nextPatientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPatientButtonActionPerformed
        try {
            if (queueList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Queue is empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // End any current consulting
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && "Consulting".equals(node.getEntry().getStatus())) {
                    node.getEntry().markConsultationDone();
                    // Optionally keep 'Done' entry for history or remove; we'll remove to advance queue
                    queueList.deleteAtPosition(i);
                    break;
                }
            }
            // Promote first waiting to Consulting
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && "Waiting".equals(node.getEntry().getStatus())) {
                    node.getEntry().markConsulting();
                    break;
                }
            }
            saveQueueData();
            refreshQueueTable();
            updateStatistics();
            if (consultationPanel != null) consultationPanel.refreshConsultationDisplay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calling next patient: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_nextPatientButtonActionPerformed

    private void startConsultationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startConsultationButtonActionPerformed
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the queue to start consultation.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String queueNumber = (String) queueTable.getValueAt(selectedRow, 0);
            String status = (String) queueTable.getValueAt(selectedRow, 3);
            
            // Check if patient is already consulting
            if ("Consulting".equals(status)) {
                JOptionPane.showMessageDialog(this, "This patient is already consulting.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Ensure only one consulting
            for (QueueEntry entry : queueList) {
                if ("Consulting".equals(entry.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Another patient is currently consulting. Please complete that consultation first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            
            // Mark selected as consulting
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && queueNumber.equals(node.getEntry().getQueueNumber())) {
                    node.getEntry().markConsulting();
                    break;
                }
            }
            saveQueueData();
            refreshQueueTable();
            updateStatistics();
            if (consultationPanel != null) consultationPanel.refreshConsultationDisplay();
            
            // Navigate to consultation panel immediately with selected patient
            if (consultationPanel != null) {
                Patient selPatient = null;
                for (int i = 1; i <= queueList.getSize(); i++) {
                    adt.Node<QueueEntry> n = queueList.getElement(i);
                    if (n != null && queueNumber.equals(n.getEntry().getQueueNumber())) {
                        selPatient = n.getEntry().getPatient();
                        break;
                    }
                }
                if (selPatient != null) {
                    consultationPanel.selectPatientById(selPatient.getPatientID());
                }
                mainFrame.showPanel("consultationPanel");
            }
            JOptionPane.showMessageDialog(this, "Patient marked as consulting. Proceeding to Consultation panel.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error starting consultation: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_startConsultationButtonActionPerformed

    private void removeFromQueueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromQueueButtonActionPerformed
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to remove from queue.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String queueNumber = (String) queueTable.getValueAt(selectedRow, 0);
            
            // Find and remove
            for (int i = 1; i <= queueList.getSize(); i++) {
                adt.Node<QueueEntry> node = queueList.getElement(i);
                if (node != null && queueNumber.equals(node.getEntry().getQueueNumber())) {
                    queueList.deleteAtPosition(i);
                    break;
                }
            }
            saveQueueData();
            refreshQueueTable();
            updateStatistics();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error removing patient from queue: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_removeFromQueueButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (waitingTimeTimer != null) {
            waitingTimeTimer.stop();
        }
        mainFrame.showPanel("consultationManagement");
    }//GEN-LAST:event_backButtonActionPerformed

    private void updateStatistics() {
        try {
            // Update total patients
            totalPatientsLabel.setText(String.valueOf(queueList != null ? queueList.getSize() : 0));
            
            // Update average waiting time
            if (queueList == null || queueList.isEmpty()) {
                averageWaitingTimeLabel.setText("0m");
            } else {
                long totalMillis = 0;
                int count = 0;
                
                for (QueueEntry entry : queueList) {
                    totalMillis += entry.getWaitingTimeMillis();
                    count++;
                }
                
                if (count > 0) {
                    long avgMillis = totalMillis / count;
                    long avgMinutes = avgMillis / (1000 * 60);
                    if (avgMinutes >= 60) {
                        long hours = avgMinutes / 60;
                        long mins = avgMinutes % 60;
                        averageWaitingTimeLabel.setText(String.format("%dh %dm", hours, mins));
                    } else {
                        averageWaitingTimeLabel.setText(String.format("%dm", avgMinutes));
                    }
                } else {
                    averageWaitingTimeLabel.setText("0m");
                }
            }
            
            // Update current queue number
            currentQueueNumberLabel.setText("E" + (lastQueueNumber + 1));
            
            // Update button states
            updateButtonStates();
        } catch (ClassCastException e) {
            System.err.println("Error: Queue data structure mismatch in statistics. Clearing queue and starting fresh.");
            queueList = new DoublyLinkedList<>();
            FileUtils.writeDataToFile("queue", queueList);
            totalPatientsLabel.setText("0");
            averageWaitingTimeLabel.setText("0m");
        }
    }
    
    private void updateButtonStates() {
        // Check if there's a patient currently consulting
        QueueEntry currentConsulting = null;
        for (QueueEntry entry : queueList) {
            if ("Consulting".equals(entry.getStatus())) {
                currentConsulting = entry;
                break;
            }
        }
        
        if (currentConsulting != null) {
            startConsultationButton.setEnabled(false);
            startConsultationButton.setToolTipText("Another patient is currently consulting");
        } else {
            startConsultationButton.setEnabled(true);
            startConsultationButton.setToolTipText("Start consultation with selected patient");
        }
        
        if (queueList == null || queueList.isEmpty()) {
            nextPatientButton.setEnabled(false);
            nextPatientButton.setToolTipText("No patients in queue");
        } else {
            nextPatientButton.setEnabled(true);
            nextPatientButton.setToolTipText("Call next patient from queue");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToQueueButton;
    private javax.swing.JLabel averageWaitingTimeLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel currentQueueNumberLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton nextPatientButton;
    private javax.swing.JComboBox<String> patientComboBox;
    private javax.swing.JButton removeFromQueueButton;
    private javax.swing.JButton startConsultationButton;
    private javax.swing.JTable queueTable;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel totalPatientsLabel;
    // End of variables declaration//GEN-END:variables
} 