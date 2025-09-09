package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import control.ConsultationController.ConsultationControl;
import enitity.Prescription;
import enitity.PrescriptionItem;
import enitity.Medicine;
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
 * Prescription Management Panel
 * @author Zhen Bang
 */
public class PrescriptionPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private JTable prescriptionTable;
    private JTable itemsTable;
    private JScrollPane prescriptionScrollPane;
    private JScrollPane itemsScrollPane;
    private JPanel dataPanel;
    private JComboBox<String> consultationComboBox;
    private JComboBox<String> medicineComboBox;
    private JTextField diagnosisField;
    private JTextField instructionsField;
    private JSpinner quantitySpinner;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JSpinner durationSpinner;
    private DoublyLinkedList<Pair<String, PrescriptionItem>> currentPrescriptionItems;
    
    /**
     * Creates new form PrescriptionPanel
     */
    public PrescriptionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.consultationControl = new ConsultationControl();
        this.currentPrescriptionItems = new DoublyLinkedList<>();
        
        initComponents();
        // set header logo image
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        
        // Initialize the prescription management interface
        initializePrescriptionInterface();
        loadPrescriptions();
    }

    public void reloadData() {
        // Ensure latest data from disk (consultations, prescriptions, queue)
        consultationControl.reloadConsultations();
        consultationControl.reloadPrescriptions();
        consultationControl.reloadQueue();
        loadPrescriptions();
        loadPrescriptionItems();
        populateConsultationComboBox();
    }

    public void reloadPrescriptionData() {
        reloadData();
    }
    
    private void initializePrescriptionInterface() {
        // Create the data panel
        dataPanel = new JPanel(new BorderLayout());
        
        // Create prescription table
        String[] prescriptionColumns = {"ID", "Patient", "Doctor", "Date", "Diagnosis", "Status", "Total Cost"};
        DefaultTableModel prescriptionModel = new DefaultTableModel(prescriptionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prescriptionTable = new JTable(prescriptionModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionTable.getTableHeader().setReorderingAllowed(false);
        
        prescriptionScrollPane = new JScrollPane(prescriptionTable);
        prescriptionScrollPane.setPreferredSize(new Dimension(600, 80));
        
        // Create items table
        String[] itemsColumns = {"Medicine", "Quantity", "Duration", "Unit Price", "Total"};
        DefaultTableModel itemsModel = new DefaultTableModel(itemsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(itemsModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.getTableHeader().setReorderingAllowed(false);
        
        itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(600, 80));
        
        // Create input panels
        JPanel prescriptionInputPanel = createPrescriptionInputPanel();
        JPanel medicineInputPanel = createMedicineInputPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create tables panel
        JPanel tablesPanel = new JPanel(new BorderLayout());
        tablesPanel.add(prescriptionScrollPane, BorderLayout.NORTH);
        tablesPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
        // Create main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        // Place prescription details on the left and add medicine item on the right
        JPanel inputPanels = new JPanel(new java.awt.GridLayout(1, 2, 20, 0));
        inputPanels.add(prescriptionInputPanel);
        inputPanels.add(medicineInputPanel);
        mainContentPanel.add(inputPanels, BorderLayout.NORTH);
        mainContentPanel.add(tablesPanel, BorderLayout.CENTER);
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
    
    private JPanel createPrescriptionInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        
        // Initialize components
        consultationComboBox = new JComboBox<>();
        diagnosisField = new JTextField(20);
        instructionsField = new JTextField(20);
        diagnosisField.setPreferredSize(new Dimension(150, 25));
        instructionsField.setPreferredSize(new Dimension(150, 25));
        
        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Consultation:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(consultationComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(diagnosisField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Instructions:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(instructionsField, gbc);
        
        // Populate consultation combo box
        populateConsultationComboBox();
        
        return panel;
    }
    
    private JPanel createMedicineInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add Medicine Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        
        // Initialize components (dosage and frequency removed)
        medicineComboBox = new JComboBox<>();
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        // Ensure inputs render at a usable width in small windows
        medicineComboBox.setPreferredSize(new Dimension(200, 25));
        quantitySpinner.setPreferredSize(new Dimension(70, 25));
        durationSpinner.setPreferredSize(new Dimension(70, 25));
        
        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Medicine:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(medicineComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(quantitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Duration (days):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(durationSpinner, gbc);
        
        // Populate medicine combo box
        populateMedicineComboBox();
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addPrescriptionButton = new JButton("Add Prescription");
        JButton addMedicineButton = new JButton("Add Medicine");
        JButton removeMedicineButton = new JButton("Remove Medicine");
        JButton completeAndRemoveButton = new JButton("Complete & Remove from Queue");
        JButton backButton = new JButton("Back");
        
        addPrescriptionButton.addActionListener(e -> addPrescription());
        addMedicineButton.addActionListener(e -> addMedicineItem());
        removeMedicineButton.addActionListener(e -> removeMedicineItem());
        completeAndRemoveButton.addActionListener(e -> completeAndRemoveFromQueue());
        backButton.addActionListener(e -> mainFrame.showPanel("consultationManagement"));
        
        panel.add(addPrescriptionButton);
        panel.add(addMedicineButton);
        panel.add(removeMedicineButton);
        panel.add(completeAndRemoveButton);
        panel.add(backButton);
        
        return panel;
    }
    
    private void completeAndRemoveFromQueue() {
        // Get the current prescribing patient
        enitity.QueueEntry currentPrescriptioning = consultationControl.getCurrentPrescriptioningPatient();
        
        if (currentPrescriptioning == null) {
            JOptionPane.showMessageDialog(this, "No patient currently in prescribing phase.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Complete consultation and prescription for " + currentPrescriptioning.getPatient().getPatientName() + "?\n" +
            "This will mark the consultation and prescription process as complete.", 
            "Complete Process", JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            // Complete the consultation and prescription process
            boolean success = consultationControl.completeConsultationAndPrescription(currentPrescriptioning.getQueueNumber());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Patient " + currentPrescriptioning.getPatient().getPatientName() + " has been removed from queue.\n" +
                    "Consultation and prescription process completed.", 
                    "Process Completed", JOptionPane.INFORMATION_MESSAGE);
                
                // Auto-mark the latest consultation for this patient as Completed
                try {
                    // Get consultations for this patient
                    DoublyLinkedList<Pair<String, Consultation>> list = consultationControl.getConsultationsByPatient(currentPrescriptioning.getPatient().getPatientID());
                    if (!list.isEmpty()) {
                        // Get the latest consultation (last in the list)
                        Consultation latestConsultation = list.getLast().getEntry().getValue();
                        consultationComboBox.addItem(latestConsultation.getConsultationID() + " - " + latestConsultation.getFormattedDateTime());
                    }
                } catch (Exception ignore) {}

                // Return to queue panel
                mainFrame.showPanel("queuePanel");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to complete the process.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void populateConsultationComboBox() {
        consultationComboBox.removeAllItems();
        
        // If there's a prescribing patient from queue, preselect and lock it
        enitity.QueueEntry prescriptioningPatient = consultationControl.getCurrentPrescriptioningPatient();
        if (prescriptioningPatient != null) {
            DoublyLinkedList<Pair<String, Consultation>> consultations = consultationControl.getAllConsultations();
            for (Pair<String, Consultation> pair : consultations) {
                Consultation consultation = pair.getValue();
                if (consultation.getPatient() != null && 
                    consultation.getPatient().getPatientID().equals(prescriptioningPatient.getPatient().getPatientID())) {
                    String item = consultation.getConsultationID() + " - " + 
                                 consultation.getPatient().getPatientName() + " (FROM QUEUE)";
                    consultationComboBox.addItem(item);
                    consultationComboBox.setSelectedItem(item);
                    consultationComboBox.setEnabled(false);
                    break;
                }
            }
        } else {
            consultationComboBox.setEnabled(true);
        }
        
        // Add all other consultations
        DoublyLinkedList<Pair<String, Consultation>> consultations = consultationControl.getAllConsultations();
        for (Pair<String, Consultation> pair : consultations) {
            Consultation consultation = pair.getValue();
            String item = consultation.getConsultationID() + " - " + 
                         (consultation.getPatient() != null ? consultation.getPatient().getPatientName() : "N/A");
            Object selectedItem = consultationComboBox.getSelectedItem();
            if (selectedItem == null || !selectedItem.equals(item)) {
                consultationComboBox.addItem(item);
            }
        }
    }
    
    private void populateMedicineComboBox() {
        medicineComboBox.removeAllItems();
        
        DoublyLinkedList<Pair<String, Medicine>> medicines = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
        if (medicines != null) {
            for (Pair<String, Medicine> pair : medicines) {
                Medicine medicine = pair.getValue();
                String item = medicine.getMedicineId() + " - " + medicine.getName() + " (" + medicine.getFormulation() + ")";
                medicineComboBox.addItem(item);
            }
        }
    }
    
    private void loadPrescriptions() {
        if (prescriptionTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) prescriptionTable.getModel();
        model.setRowCount(0);
        
        DoublyLinkedList<Pair<String, Prescription>> prescriptions = consultationControl.getAllPrescriptions();
        
        for (Pair<String, Prescription> pair : prescriptions) {
            Prescription prescription = pair.getValue();
            Object[] row = {
                prescription.getPrescriptionID(),
                prescription.getPatient() != null ? prescription.getPatient().getPatientName() : "N/A",
                prescription.getDoctor() != null ? prescription.getDoctor().getName() : "N/A",
                prescription.getFormattedDate(),
                prescription.getDiagnosis(),
                prescription.getStatus(),
                String.format("RM %.2f", prescription.getTotalCost())
            };
            model.addRow(row);
        }
    }
    
    private void loadPrescriptionItems() {
        if (itemsTable == null) return;
        
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        model.setRowCount(0);
        
        for (Pair<String, PrescriptionItem> pair : currentPrescriptionItems) {
            PrescriptionItem item = pair.getValue();
            Object[] row = {
                item.getMedicineName(),
                item.getQuantity(),
                item.getDuration() + " days",
                String.format("RM %.2f", item.getUnitPrice()),
                String.format("RM %.2f", item.getTotalCost())
            };
            model.addRow(row);
        }
    }
    
    private Consultation getSelectedConsultation() {
        String selected = (String) consultationComboBox.getSelectedItem();
        if (selected != null) {
            String consultationID = selected.split(" - ")[0];
            return consultationControl.getConsultation(consultationID);
        }
        return null;
    }
    
    private Medicine getSelectedMedicine() {
        String selected = (String) medicineComboBox.getSelectedItem();
        if (selected != null) {
            String medicineID = selected.split(" - ")[0];
            DoublyLinkedList<Pair<String, Medicine>> medicines = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
            if (medicines != null) {
                for (Pair<String, Medicine> pair : medicines) {
                    if (pair.getKey().equals(medicineID)) {
                        return pair.getValue();
                    }
                }
            }
        }
        return null;
    }
    
    private void addPrescription() {
        try {
            Consultation consultation = getSelectedConsultation();
            if (consultation == null) {
                JOptionPane.showMessageDialog(this, "Please select a consultation.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String diagnosis = diagnosisField.getText().trim();
            String instructions = instructionsField.getText().trim();
            
            if (diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter diagnosis.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (currentPrescriptionItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one medicine item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create prescription
            Prescription prescription = consultationControl.createPrescription(consultation, diagnosis, instructions);
            
            // Add prescription items
            for (Pair<String, PrescriptionItem> itemPair : currentPrescriptionItems) {
                PrescriptionItem item = itemPair.getValue();
                consultationControl.addPrescriptionItem(prescription.getPrescriptionID(),
                        item.getMedicine(), item.getQuantity(), item.getDosage(),
                        item.getFrequency(), item.getDuration());
            }
            
            JOptionPane.showMessageDialog(this, "Prescription created successfully!\nID: " + prescription.getPrescriptionID(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearFields();
            loadPrescriptions();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating prescription: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addMedicineItem() {
        try {
            Medicine medicine = getSelectedMedicine();
            if (medicine == null) {
                JOptionPane.showMessageDialog(this, "Please select a medicine.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = (Integer) quantitySpinner.getValue();
            int duration = (Integer) durationSpinner.getValue();
            
            if (quantity <= 0 || duration <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and duration must be positive numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create prescription item
            PrescriptionItem item = new PrescriptionItem(medicine, quantity, "", "", String.valueOf(duration));
            
            // Add to current prescription items
            String itemID = "ITEM" + (currentPrescriptionItems.getSize() + 1);
            currentPrescriptionItems.insertLast(new Pair<>(itemID, item));
            
            // Refresh items table
            loadPrescriptionItems();
            
            // Clear input fields
            quantitySpinner.setValue(1);
            durationSpinner.setValue(1);
            
            JOptionPane.showMessageDialog(this, "Medicine item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and duration.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding medicine item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeMedicineItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine item to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Remove from current prescription items
        currentPrescriptionItems.deleteAtPosition(selectedRow + 1); // Convert to 1-based indexing
        
        // Refresh items table
        loadPrescriptionItems();
        
        JOptionPane.showMessageDialog(this, "Medicine item removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearFields() {
        consultationComboBox.setSelectedIndex(0);
        diagnosisField.setText("");
        instructionsField.setText("");
        
        // Clear current prescription items
        currentPrescriptionItems = new DoublyLinkedList<>();
        loadPrescriptionItems();
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
        titleLabel.setText(" Prescription Management");
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
