/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.ConsultationManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import control.ConsultationControl;
import enitity.Consultation;
import enitity.Patient;
import enitity.Doctor;
import enitity.Medicine;
import enitity.Prescription;
import enitity.PrescriptionItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import utility.FileUtils;

/**
 * Prescription Management Panel for Consultation Module
 * @author Zhen Bang
 */
public class PrescriptionPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ConsultationControl consultationControl;
    private DoublyLinkedList<Pair<String, Patient>> patientList;
    private DoublyLinkedList<Pair<String, Doctor>> doctorList;
    private DoublyLinkedList<Pair<String, Consultation>> consultationList;
    private DoublyLinkedList<Pair<String, Medicine>> medicineList;
    private DoublyLinkedList<Pair<String, PrescriptionItem>> prescriptionItems;
    private DefaultTableModel prescriptionTableModel;
    private DefaultTableModel itemsTableModel;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    /**
     * Creates new form PrescriptionPanel
     */
    public PrescriptionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.consultationControl = new ConsultationControl();
        this.prescriptionItems = new DoublyLinkedList<>();
        
        // Initialize spinners before layout setup
        setupDateTimeSpinners();
        
        initComponents();
        setupUI();
        initializeData();
        loadPrescriptions();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(240, 248, 255));
        
        // Style title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        
        // Style buttons
        styleButton(addButton, "Add Prescription", new Color(70, 130, 180));
        styleButton(addItemButton, "Add Medicine", new Color(60, 179, 113));
        styleButton(removeItemButton, "Remove Medicine", new Color(220, 20, 60));
        styleButton(backButton, "Back", new Color(128, 128, 128));
        
        // Setup tables
        setupTables();
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
    
    private void setupTables() {
        // Setup prescription table
        String[] prescriptionColumns = {"ID", "Patient", "Doctor", "Date", "Diagnosis", "Status", "Total Cost"};
        prescriptionTableModel = new DefaultTableModel(prescriptionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prescriptionTable.setModel(prescriptionTableModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionTable.getTableHeader().setReorderingAllowed(false);
        
        // Setup prescription items table
        String[] itemsColumns = {"Medicine", "Quantity", "Dosage", "Frequency", "Duration", "Unit Price", "Total"};
        itemsTableModel = new DefaultTableModel(itemsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable.setModel(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void initializeData() {
        // Load real patient data from file
        loadPatientData();
        
        // Load real doctor data from file
        loadDoctorData();
        
        // Load real consultation data from file
        loadConsultationData();
        
        // Load real medicine data from file
        loadMedicineData();
        
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
    
    private void loadMedicineData() {
        medicineList = new DoublyLinkedList<>();
        
        // Load medicines from file
        DoublyLinkedList<Pair<String, Medicine>> loadedMedicines = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
        
        if (loadedMedicines != null && !loadedMedicines.isEmpty()) {
            System.out.println("Loading " + loadedMedicines.getSize() + " medicines from file");
            
            for (Pair<String, Medicine> medicinePair : loadedMedicines) {
                medicineList.insertLast(medicinePair);
                System.out.println("Loaded medicine: " + medicinePair.getKey() + " - " + medicinePair.getValue().getName());
            }
        } else {
            System.out.println("No medicine data found in file. Using empty list.");
        }
    }
    
    private void populateComboBoxes() {
        patientComboBox.removeAllItems();
        doctorComboBox.removeAllItems();
        consultationComboBox.removeAllItems();
        medicineComboBox.removeAllItems();
        
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
        
        // Add consultations
        System.out.println("Populating consultations...");
        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            String item = consultation.getConsultationID() + " - " + consultation.getConsultationType();
            consultationComboBox.addItem(item);
            System.out.println("Added consultation: " + item);
        }
        
        // Add medicines
        System.out.println("Populating medicines...");
        for (Pair<String, Medicine> pair : medicineList) {
            Medicine medicine = pair.getValue();
            String item = medicine.getMedicineId() + " - " + medicine.getName() + " (" + medicine.getFormulation() + ")";
            medicineComboBox.addItem(item);
            System.out.println("Added medicine: " + item);
        }
        
        // Set default selections if available
        if (patientComboBox.getItemCount() > 0) patientComboBox.setSelectedIndex(0);
        if (doctorComboBox.getItemCount() > 0) doctorComboBox.setSelectedIndex(0);
        if (consultationComboBox.getItemCount() > 0) consultationComboBox.setSelectedIndex(0);
        if (medicineComboBox.getItemCount() > 0) medicineComboBox.setSelectedIndex(0);
    }
    
    private void loadPrescriptions() {
        prescriptionTableModel.setRowCount(0);
        
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
            prescriptionTableModel.addRow(row);
        }
    }
    
    private void loadPrescriptionItems() {
        itemsTableModel.setRowCount(0);
        
        for (Pair<String, PrescriptionItem> pair : prescriptionItems) {
            PrescriptionItem item = pair.getValue();
            Object[] row = {
                item.getMedicineName(),
                item.getQuantity(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration() + " days",
                String.format("RM %.2f", item.getUnitPrice()),
                String.format("RM %.2f", item.getTotalCost())
            };
            itemsTableModel.addRow(row);
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
    
    private Medicine getSelectedMedicine() {
        String selected = (String) medicineComboBox.getSelectedItem();
        if (selected != null) {
            String medicineID = selected.split(" - ")[0];
            Object result = medicineList.findByKey(medicineID);
            if (result instanceof Medicine) {
                return (Medicine) result;
            }
        }
        return null;
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        patientComboBox = new javax.swing.JComboBox<>();
        doctorComboBox = new javax.swing.JComboBox<>();
        consultationComboBox = new javax.swing.JComboBox<>();
        diagnosisField = new javax.swing.JTextField();
        instructionsField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        prescriptionTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        itemsTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        addItemButton = new javax.swing.JButton();
        removeItemButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        medicineComboBox = new javax.swing.JComboBox<>();
        quantityField = new javax.swing.JTextField();
        dosageField = new javax.swing.JTextField();
        frequencyField = new javax.swing.JTextField();
        durationField = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(800, 600));

        titleLabel.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Prescription Management");

        prescriptionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Patient", "Doctor", "Date", "Diagnosis", "Status", "Total Cost"
            }
        ));
        jScrollPane1.setViewportView(prescriptionTable);

        itemsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Medicine", "Quantity", "Dosage", "Frequency", "Duration", "Unit Price", "Total"
            }
        ));
        jScrollPane2.setViewportView(itemsTable);

        addButton.setText("Add Prescription");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        addItemButton.setText("Add Medicine");
        addItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemButtonActionPerformed(evt);
            }
        });

        removeItemButton.setText("Remove Medicine");
        removeItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemButtonActionPerformed(evt);
            }
        });

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Prescription Details"));

        jLabel1.setText("Patient:");

        jLabel2.setText("Doctor:");

        jLabel3.setText("Consultation:");

        jLabel4.setText("Diagnosis:");

        jLabel5.setText("Instructions:");

        jLabel6.setText("Valid Until:");

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
                    .addComponent(consultationComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(diagnosisField)
                    .addComponent(instructionsField)
                    .addComponent(dateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(consultationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(diagnosisField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(instructionsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(timeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Medicine Item"));

        jLabel8.setText("Medicine:");

        jLabel9.setText("Quantity:");

        jLabel10.setText("Dosage:");

        jLabel11.setText("Frequency:");

        jLabel12.setText("Duration (days):");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(medicineComboBox, 0, 200, Short.MAX_VALUE)
                    .addComponent(quantityField)
                    .addComponent(dosageField)
                    .addComponent(frequencyField)
                    .addComponent(durationField))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(medicineComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(dosageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(frequencyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(addItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(removeItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            
            if (prescriptionItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one medicine item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create prescription
            Prescription prescription = consultationControl.createPrescription(consultation, diagnosis, instructions);
            
            // Add prescription items
            for (Pair<String, PrescriptionItem> itemPair : prescriptionItems) {
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
    }//GEN-LAST:event_addButtonActionPerformed

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
        try {
            Medicine medicine = getSelectedMedicine();
            
            if (medicine == null) {
                JOptionPane.showMessageDialog(this, "Please select a medicine.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String quantityStr = quantityField.getText().trim();
            String dosage = dosageField.getText().trim();
            String frequency = frequencyField.getText().trim();
            String durationStr = durationField.getText().trim();
            
            if (quantityStr.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || durationStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all medicine item fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            int duration = Integer.parseInt(durationStr);
            
            if (quantity <= 0 || duration <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and duration must be positive numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create prescription item
            PrescriptionItem item = new PrescriptionItem(medicine, quantity, dosage, frequency, String.valueOf(duration));
            
            // Add to prescription items list
            String itemID = "ITEM" + (prescriptionItems.getSize() + 1);
            prescriptionItems.insertLast(new Pair<>(itemID, item));
            
            // Refresh items table
            loadPrescriptionItems();
            
            // Clear medicine item fields
            quantityField.setText("");
            dosageField.setText("");
            frequencyField.setText("");
            durationField.setText("");
            
            JOptionPane.showMessageDialog(this, "Medicine item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and duration.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding medicine item: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addItemButtonActionPerformed

    private void removeItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemButtonActionPerformed
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine item to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Remove from prescription items list
        prescriptionItems.deleteAtPosition(selectedRow + 1); // Convert to 1-based indexing
        
        // Refresh items table
        loadPrescriptionItems();
        
        JOptionPane.showMessageDialog(this, "Medicine item removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_removeItemButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.showPanel("consultationManagement");
    }//GEN-LAST:event_backButtonActionPerformed

    private void clearFields() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        consultationComboBox.setSelectedIndex(0);
        diagnosisField.setText("");
        instructionsField.setText("");
        
        // Reset date and time spinners to current date/time
        Calendar calendar = Calendar.getInstance();
        dateSpinner.setValue(calendar.getTime());
        timeSpinner.setValue(calendar.getTime());
        
        // Clear prescription items
        prescriptionItems = new DoublyLinkedList<>();
        loadPrescriptionItems();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addItemButton;
    private javax.swing.JButton backButton;
    private javax.swing.JComboBox<String> consultationComboBox;
    private javax.swing.JTextField diagnosisField;
    private javax.swing.JComboBox<String> doctorComboBox;
    private javax.swing.JTextField dosageField;
    private javax.swing.JTextField durationField;
    private javax.swing.JTextField frequencyField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable itemsTable;
    private javax.swing.JTextField instructionsField;
    private javax.swing.JComboBox<String> medicineComboBox;
    private javax.swing.JComboBox<String> patientComboBox;
    private javax.swing.JTable prescriptionTable;
    private javax.swing.JTextField quantityField;
    private javax.swing.JButton removeItemButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
} 