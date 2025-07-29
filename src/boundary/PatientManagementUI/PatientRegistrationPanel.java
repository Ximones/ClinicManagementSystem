/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.PatientManagementUI;

import utility.ImageUtils;
import boundary.MainFrame;
import enitity.Patient;
import adt.DoublyLinkedList;
import adt.Pair; // Assuming Pair is in adt package
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*; // Import for file I/O
/**
 *
 * @author szepi
 */
public class PatientRegistrationPanel extends javax.swing.JPanel {

     private MainFrame mainFrame;
     private DoublyLinkedList<Patient> patientList = new DoublyLinkedList<>();
     private DefaultTableModel tableModel;
     
      private static final String DATA_FILE_NAME = "patients.ser"; // Using .ser for serialized objects
     
    /**
     * Creates new form PatientRegistrationPanel
     */
    public PatientRegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
 
        
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        setupTable();
        loadAndDisplayPatients();
        jButton1.addActionListener(e -> addPatient());
            
    }
    
    
    private void setupTable() {
        // Initialize DefaultTableModel with column headers, including "Status"
        tableModel = new DefaultTableModel(
                new Object[]{"Patient ID", "Name", "Age", "IC", "Gender", "Contact", "Email", "Address", "Date of Reg", "Status"}, 0);
        patientTable.setModel(tableModel);
    }
    
    
    private void loadAndDisplayPatients() {
        int maxId = 0; // To track the highest patient index for auto-generation

        // Clear existing table data and in-memory list before loading
        tableModel.setRowCount(0);
        patientList.clear(); // Important: Clear the ADT list to avoid duplicates on reload

        File file = new File(DATA_FILE_NAME);

        // Check if the file exists and is not empty before attempting to load
        if (file.exists() && file.length() > 0) {
            try (FileInputStream fis = new FileInputStream(DATA_FILE_NAME);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                Object obj = ois.readObject();
                if (obj instanceof DoublyLinkedList) {
                    patientList = (DoublyLinkedList<Patient>) obj;
                    System.out.println("Patient data loaded from " + DATA_FILE_NAME);

                    // Re-populate the table and find the max ID
                    for (Patient p : patientList) { // Corrected for-each loop syntax
                        tableModel.addRow(new Object[]{
                            p.getPatientID(),
                            p.getPatientName(),
                            p.getPatientAge(),
                            p.getPatientIC(),
                            p.getGender(),
                            p.getContact(),
                            p.getEmail(),
                            p.getAddress(),
                            p.getDateOfRegistration(),
                            p.getStatus()
                        });

                        // Update patientIndex based on loaded IDs
                        try {
                            String idNumStr = p.getPatientID().substring(1); // "001"
                            int currentIdNum = Integer.parseInt(idNumStr); // 1
                            if (currentIdNum > maxId) {
                                maxId = currentIdNum;
                            }
                        } catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
                            System.err.println("Warning: Could not parse patient ID for index tracking during load: " + p.getPatientID());
                        }
                    }
                    // Set the patientIndex in the Patient class to ensure new IDs continue correctly
                    Patient.setPatientIndex(maxId);

                } else {
                    System.err.println("Loaded object is not a DoublyLinkedList of Patients. Starting with a new list.");
                    // Fallback to empty list if type mismatch or corrupted data
                    patientList = new DoublyLinkedList<>();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading patient data: " + e.getMessage());
                e.printStackTrace();
                // If an error occurs during load, initialize with an empty list
                patientList = new DoublyLinkedList<>();
            }
        } else {
            System.out.println("Patient data file not found or is empty. Starting with a new list.");
            // If file doesn't exist or is empty, patientList is already initialized as empty
        }
    }
    
    
     public void savePatientData() {
        try (FileOutputStream fos = new FileOutputStream(DATA_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(patientList); // Write the entire DoublyLinkedList object
            System.out.println("Patient data saved to " + DATA_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error saving patient data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving patient data: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    
    
    private void addPatient() {
        // Create an instance of PatientDialog, passing the parent frame and setting it as modal
        PatientDialog dialog = new PatientDialog(mainFrame, true); // Use mainFrame as parent for proper centering
        dialog.setVisible(true);

        // Retrieve the result after the dialog closes
        Pair<String, Patient> patientResult = dialog.getResult();

        // Check if the dialog was saved (not cancelled)
        if (patientResult != null) {
            Patient newPatient = patientResult.getValue(); // Get the Patient object from the Pair
            
            // Add the new patient to the in-memory list
            // Note: loadAndDisplayPatients() will clear and re-add all, so direct insert here might be redundant
            // if loadAndDisplayPatients() is called immediately after.
            // However, inserting here maintains list integrity before a full reload.
            patientList.insertLast(newPatient); 
            
            // After adding, save the updated list to the file
            savePatientData();
            
            // After adding and saving, reload and display all patients to reflect changes
            loadAndDisplayPatients();

            JOptionPane.showMessageDialog(this, "Patient " + newPatient.getPatientName() + " (ID: " + newPatient.getPatientID() + ") added successfully!");
        } else {
            // User cancelled the dialog
            JOptionPane.showMessageDialog(this, "Patient registration cancelled.");
        }
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
        searchWrapperPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new java.awt.TextField();
        patientTablePanel = new javax.swing.JScrollPane();
        patientTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Patient Registration ");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        filterLabel.setText("Filter By :");
        searchPanel.add(filterLabel);

        filterBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filterBox.setSelectedIndex(-1);
        searchPanel.add(filterBox);

        filterField.setColumns(15);
        filterField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        filterField.setText("                                     ");
        filterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterFieldActionPerformed(evt);
            }
        });
        searchPanel.add(filterField);

        searchWrapperPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        patientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        patientTable.setMaximumSize(new java.awt.Dimension(0, 0));
        patientTable.setMinimumSize(new java.awt.Dimension(0, 0));
        patientTable.setPreferredSize(new java.awt.Dimension(0, 0));
        patientTablePanel.setViewportView(patientTable);

        searchWrapperPanel.add(patientTablePanel, java.awt.BorderLayout.CENTER);

        jButton1.setText("Add Patient");
        ButtonPanel.add(jButton1);

        jButton2.setText("Done");
        ButtonPanel.add(jButton2);

        jButton3.setText("jButton3");
        ButtonPanel.add(jButton3);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JComboBox<String> filterBox;
    private java.awt.TextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTable patientTable;
    private javax.swing.JScrollPane patientTablePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
