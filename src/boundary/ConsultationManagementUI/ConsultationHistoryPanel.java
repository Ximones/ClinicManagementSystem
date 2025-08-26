package boundary.ConsultationManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import control.ConsultationControl;
import enitity.Consultation;
import enitity.Patient;
import enitity.Prescription;
import java.awt.BorderLayout;
import java.awt.Dimension;
import utility.ImageUtils;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import utility.FileUtils;

public class ConsultationHistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private JTable patientTable;
    private JTable consultationTable;
    private JTable prescriptionTable;
    private DefaultTableModel patientModel;
    private DefaultTableModel consultationModel;
    private DefaultTableModel prescriptionModel;
    private JComboBox<String> doctorFilter;
    private JComboBox<String> statusFilter;

    public ConsultationHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initTables();
        buildLayout();
        reloadData();
    }

    private void initTables() {
        patientModel = new DefaultTableModel(new Object[]{"Patient ID", "Name", "IC"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        consultationModel = new DefaultTableModel(new Object[]{
            "Consultation ID", "Date/Time", "Doctor", "Type", "Status", "Diagnosis"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        prescriptionModel = new DefaultTableModel(new Object[]{
            "Prescription ID", "Date", "Doctor", "Diagnosis", "Items", "Total"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        patientTable = new JTable(patientModel);
        consultationTable = new JTable(consultationModel);
        prescriptionTable = new JTable(prescriptionModel);

        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    onPatientSelected();
                }
            }
        });
    }

    private void buildLayout() {
        // Top controls (Back + Filters)
        JPanel topBar = new JPanel();
        javax.swing.JLabel logoLabel = new javax.swing.JLabel();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.showPanel("consultationManagement");
            }
        });
        doctorFilter = new JComboBox<>();
        statusFilter = new JComboBox<>();
        // Style to be consistent with other modules
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(200, 35));
        topBar.add(logoLabel);
        topBar.add(backButton);
        topBar.add(new JLabel("Doctor:"));
        topBar.add(doctorFilter);
        topBar.add(new JLabel("Status:"));
        topBar.add(statusFilter);
        add(topBar, BorderLayout.NORTH);

        JScrollPane left = new JScrollPane(patientTable);
        left.setPreferredSize(new Dimension(280, 400));

        JPanel right = new JPanel(new BorderLayout());
        JScrollPane topRight = new JScrollPane(consultationTable);
        JScrollPane bottomRight = new JScrollPane(prescriptionTable);

        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRight, bottomRight);
        vertical.setResizeWeight(0.5);
        right.add(vertical, BorderLayout.CENTER);

        JSplitPane root = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        root.setResizeWeight(0.35);
        add(root, BorderLayout.CENTER);
    }

    public void reloadData() {
        // Load patients from file
        patientModel.setRowCount(0);
        DoublyLinkedList<Patient> loadedPatients = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
        if (loadedPatients != null) {
            for (Patient p : loadedPatients) {
                patientModel.addRow(new Object[]{p.getPatientID(), p.getPatientName(), p.getPatientIC()});
            }
        }
        // Populate filters from current data
        populateFilters();
        // Clear detail tables until user selects a patient
        consultationModel.setRowCount(0);
        prescriptionModel.setRowCount(0);
    }

    private void populateFilters() {
        // Build doctor list from consultations store
        doctorFilter.removeAllItems();
        statusFilter.removeAllItems();
        doctorFilter.addItem("All");
        statusFilter.addItem("All");

        DoublyLinkedList<Pair<String, Consultation>> consultations
            = (DoublyLinkedList<Pair<String, Consultation>>) FileUtils.readDataFromFile("consultations");
        if (consultations != null) {
            java.util.LinkedHashSet<String> doctors = new java.util.LinkedHashSet<>();
            java.util.LinkedHashSet<String> statuses = new java.util.LinkedHashSet<>();
            for (Pair<String, Consultation> pair : consultations) {
                Consultation c = pair.getValue();
                if (c == null) continue;
                if (c.getDoctor() != null) doctors.add(c.getDoctor().getName());
                if (c.getStatus() != null) {
                    String s = c.getStatus();
                    if ("Scheduled".equals(s) || "In Progress".equals(s) || "Completed".equals(s) || "Cancelled".equals(s)) {
                        statuses.add(s);
                    }
                }
            }
            for (String d : doctors) doctorFilter.addItem(d);
            for (String s : statuses) statusFilter.addItem(s);
        }

        java.awt.event.ActionListener filterListener = e -> onPatientSelected();
        for (java.awt.event.ActionListener l : doctorFilter.getActionListeners()) doctorFilter.removeActionListener(l);
        for (java.awt.event.ActionListener l : statusFilter.getActionListeners()) statusFilter.removeActionListener(l);
        doctorFilter.addActionListener(filterListener);
        statusFilter.addActionListener(filterListener);
    }

    private void onPatientSelected() {
        int row = patientTable.getSelectedRow();
        if (row < 0) { return; }
        String patientId = patientModel.getValueAt(row, 0).toString();

        // Use ConsultationControl to read current persisted data
        ConsultationControl control = new ConsultationControl();

        // Populate consultations for patient
        consultationModel.setRowCount(0);
        String selectedDoctor = doctorFilter.getSelectedItem() != null ? doctorFilter.getSelectedItem().toString() : "All";
        String selectedStatus = statusFilter.getSelectedItem() != null ? statusFilter.getSelectedItem().toString() : "All";
        for (Consultation c : control.getConsultationsByPatient(patientId)) {
            String doctorName = c.getDoctor() != null ? c.getDoctor().getName() : "N/A";
            if (!"All".equals(selectedDoctor) && !doctorName.equals(selectedDoctor)) continue;
            if (!"All".equals(selectedStatus) && c.getStatus() != null && !c.getStatus().equals(selectedStatus)) continue;
            consultationModel.addRow(new Object[]{
                c.getConsultationID(),
                c.getFormattedDateTime(),
                doctorName,
                c.getConsultationType(),
                c.getStatus(),
                c.getDiagnosis()
            });
        }

        // Populate prescriptions for patient
        prescriptionModel.setRowCount(0);
        DoublyLinkedList<Pair<String, Prescription>> allPrescriptions = control.getPrescriptionList();
        if (allPrescriptions != null) {
            for (Pair<String, Prescription> pp : allPrescriptions) {
                Prescription p = pp.getValue();
                if (p.getPatient() != null && patientId.equals(p.getPatient().getPatientID())) {
                    String doctorName = p.getDoctor() != null ? p.getDoctor().getName() : "N/A";
                    if (!"All".equals(selectedDoctor) && !doctorName.equals(selectedDoctor)) continue;
                    int itemCount = p.getPrescriptionItems() != null ? p.getPrescriptionItems().size() : 0;
                    String total = String.format("RM %.2f", p.getTotalCost());
                    prescriptionModel.addRow(new Object[]{
                        p.getPrescriptionID(),
                        p.getFormattedDate(),
                        doctorName,
                        p.getDiagnosis(),
                        itemCount,
                        total
                    });
                }
            }
        }
    }
}


