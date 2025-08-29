package boundary.PharmacyManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.DispenseRecord;
import enitity.Medicine;
import enitity.Prescription;
import enitity.PrescriptionItem;
import utility.FileUtils;
import utility.ImageUtils;
import control.ConsultationControl;
import java.awt.Color;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import javax.swing.table.DefaultTableCellRenderer;
import utility.ReportGenerator;

/**
 *
 * @author Lee Wan Ching
 */
public class DispenseMedicinePanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private DoublyLinkedList<Pair<String, Medicine>> masterMedicineList;
    private String patientName;
    private String doctorName;
    private ConsultationControl consultationControl;
    private DoublyLinkedList<Pair<String, Prescription>> prescriptionList;
    private java.util.List<Pair<String, Prescription>> displayedPrescriptions = new java.util.ArrayList<>();

    /**
     * Creates new form DispenseMedicinePanel
     */
    public DispenseMedicinePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        initComponents();
        loadInitialComponent();
        loadInitialData();
        populateFromPrescriptions();
    }

    private void loadInitialComponent() {
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) { // Select checkbox
                    return true; // allow editor to run for all rows
                }
                return column == 10; // "Details" button
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Boolean.class;   // Select checkbox
                    case 6:
                        return Integer.class;   // #Items
                    case 7:
                        return Integer.class;   // Total Qty
                    case 8:
                        return Double.class;    // Total Price
                    case 9:
                        return String.class;    // Status
                    default:
                        return String.class;
                }
            }

        };

        model.addColumn("Select");
        model.addColumn("Prescription ID");
        model.addColumn("Patient");
        model.addColumn("Doctor");
        model.addColumn("Date");
        model.addColumn("Diagnosis");
        model.addColumn("#Items");
        model.addColumn("Total Qty");
        model.addColumn("Total Price (RM)");
        model.addColumn("Status");
        model.addColumn("Details");

        medicineTable.setModel(model);
        medicineTable.getTableHeader().setReorderingAllowed(false);

        medicineTable.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JCheckBox cb = new JCheckBox();
            cb.setHorizontalAlignment(JCheckBox.CENTER);
            cb.setSelected(Boolean.TRUE.equals(value));
            String status = (String) table.getValueAt(row, 9);
            cb.setEnabled(!"Dispensed".equals(status));
            if ("Dispensed".equals(status)) {
                cb.setBackground(Color.LIGHT_GRAY);
            } else {
                cb.setBackground(Color.WHITE);
            }
            return cb;
        });

        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(20);

        addDetailsButtonToTable();

        // Format Total Price column to 2 decimal places
        medicineTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format("%.2f", ((Number) value).doubleValue()));
                } else {
                    setText("0.00");
                }
            }

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT); // Keep text right-aligned
                return this;
            }
        });

        medicineTable.getColumnModel().getColumn(0).setCellEditor(new SelectCheckboxEditor());

    }

    private void loadInitialData() {
        DoublyLinkedList<Pair<String, Medicine>> medicineList
                = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
        if (medicineList == null) {
            medicineList = new DoublyLinkedList<>();
        }
        masterMedicineList = medicineList;

        consultationControl = new ConsultationControl();
        DoublyLinkedList<Pair<String, Prescription>> pList = consultationControl.getAllPrescriptions();
        if (pList == null) {
            pList = new DoublyLinkedList<>();
        }
        prescriptionList = pList;
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString()); // show "View"
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {

        private JButton button;
        private int row;

        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(e -> {
                stopCellEditing();
                Pair<String, Prescription> pair = displayedPrescriptions.get(row);
                showPrescriptionDetails(pair.getValue());
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private void addDetailsButtonToTable() {
        medicineTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        medicineTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor());
    }

    private void populateFromPrescriptions() {
        DefaultTableModel model = (DefaultTableModel) medicineTable.getModel();
        model.setRowCount(0);
        displayedPrescriptions.clear();

        for (Pair<String, Prescription> pair : prescriptionList) {
            Prescription p = pair.getValue();

            if (!"Pending".equals(p.getStatus()) && !"Dispensed".equals(p.getStatus())) {
                continue;
            }
            if (p.isExpired()) {
                continue;
            }

            int items = 0;
            int totalQty = 0;
            double totalCost = 0.0;

            if (p.getPrescriptionItems() != null) {
                items = p.getPrescriptionItems().size();
                for (PrescriptionItem it : p.getPrescriptionItems()) {
                    totalQty += safeInt(it.getQuantity());
                    totalCost += it.getTotalCost();
                }
            }

            displayedPrescriptions.add(pair);

            model.addRow(new Object[]{
                false,
                p.getPrescriptionID(),
                p.getPatient() != null ? p.getPatient().getPatientName() : "N/A",
                p.getDoctor() != null ? p.getDoctor().getName() : "N/A",
                p.getFormattedDate(),
                p.getDiagnosis(),
                items,
                totalQty,
                totalCost,
                p.getStatus(),
                "View"
            });
        }
    }

    public void reloadData() {
        // Reload master medicine list
        DoublyLinkedList<Pair<String, Medicine>> medicineList
                = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
        if (medicineList == null) {
            medicineList = new DoublyLinkedList<>();
        }
        masterMedicineList = medicineList;

        // Reload prescription list
        consultationControl = new ConsultationControl();
        DoublyLinkedList<Pair<String, Prescription>> pList = consultationControl.getAllPrescriptions();
        if (pList == null) {
            pList = new DoublyLinkedList<>();
        }
        prescriptionList = pList;

        // Refresh the table
        populateFromPrescriptions();
    }

    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private void showPrescriptionDetails(Prescription p) {
        String[] cols = {"Med ID", "Med Name", "Quantity", "Stock", "Duration", "Unit Price (RM)", "Line Total (RM)"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 2 || c == 3) {
                    return Integer.class;
                }
                if (c == 5 || c == 6) {
                    return Double.class;
                }
                return String.class;
            }
        };

        if (p.getPrescriptionItems() != null) {
            for (PrescriptionItem it : p.getPrescriptionItems()) {
                String medId = it.getMedicine() != null ? it.getMedicine().getMedicineId() : "";
                String medName = it.getMedicineName();
                int qty = safeInt(it.getQuantity());
                int stock = getStockForMedicineId(medId);
                double unit = it.getUnitPrice();
                double line = it.getTotalCost();
                m.addRow(new Object[]{
                    medId, medName, qty, stock, it.getDuration(), unit, line
                });
            }
        }

        JTable t = new JTable(m);
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format("%.2f", ((Number) value).doubleValue()));
                } else {
                    setText("0.00");
                }
            }

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT);
                return this;
            }
        };

        t.getColumnModel().getColumn(5).setCellRenderer(priceRenderer);
        t.getColumnModel().getColumn(6).setCellRenderer(priceRenderer);

        t.getTableHeader().setReorderingAllowed(false);
        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new java.awt.Dimension(720, 280));

        String header
                = "<html><b>Prescription ID:</b> " + p.getPrescriptionID()
                + "<br><b>Patient Name:</b> " + (p.getPatient() != null ? p.getPatient().getPatientName() : "N/A")
                + "<br><b>Doctor Name:</b> " + (p.getDoctor() != null ? p.getDoctor().getName() : "N/A")
                + "<br><b>Diagnosis:</b> " + (p.getDiagnosis() != null ? p.getDiagnosis() : "")
                + "<br><b>Instructions:</b> " + (p.getInstructions() != null ? p.getInstructions() : "")
                + "<br><b>Consultation Date:</b> " + p.getFormattedDate()
                + "</html>";

        JPanel panel = new JPanel(new java.awt.BorderLayout(10, 10));
        panel.add(new JLabel(header), java.awt.BorderLayout.PAGE_START);
        panel.add(sp, java.awt.BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Prescription Details", JOptionPane.PLAIN_MESSAGE);
    }

    private int getStockForMedicineId(String id) {
        if (id == null) {
            return 0;
        }
        for (Pair<String, Medicine> pair : masterMedicineList) {
            if (id.equals(pair.getKey())) {
                return pair.getValue().getQuantity();
            }
        }
        return 0;
    }

    private void handleDispense() {
        java.util.List<Pair<String, Prescription>> toDispense = new java.util.ArrayList<>();

        try {
            DefaultTableModel model = (DefaultTableModel) medicineTable.getModel();

            // 1) Collect selected prescriptions
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean selected = Boolean.TRUE.equals(model.getValueAt(i, 0));
                if (selected) {
                    toDispense.add(displayedPrescriptions.get(i));
                }
            }

            if (toDispense.isEmpty()) {
                throw new IllegalArgumentException("Please select at least one prescription to dispense.");
            }

            // 2) Validate stock
            StringBuilder issues = new StringBuilder();
            for (Pair<String, Prescription> pair : toDispense) {
                Prescription p = pair.getValue();
                if (p.getPrescriptionItems() == null || p.getPrescriptionItems().isEmpty()) {
                    issues.append("• ").append(p.getPrescriptionID()).append(": has no items.\n");
                    continue;
                }
                for (PrescriptionItem it : p.getPrescriptionItems()) {
                    String medId = it.getMedicine() != null ? it.getMedicine().getMedicineId() : null;
                    String medName = it.getMedicineName();
                    int qty = safeInt(it.getQuantity());
                    Pair<String, Medicine> mp = findMedicinePairById(medId);
                    int stock = (mp == null) ? 0 : mp.getValue().getQuantity();
                    if (qty > stock) {
                        issues.append("• ").append(p.getPrescriptionID()).append(" / ").append(medName)
                                .append(": required ").append(qty).append(", stock ").append(stock).append("\n");
                    }
                }
            }

            if (issues.length() > 0) {
                throw new IllegalArgumentException("Insufficient stock:\n" + issues);
            }

            // 3) Confirmation UI
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.add(new JLabel("<html><b>Confirm dispensing the following prescriptions?</b></html>"));

            for (Pair<String, Prescription> pair : toDispense) {
                Prescription p = pair.getValue();
                String head = "<html><br><b>Prescription ID: " + p.getPrescriptionID() + "</b> — "
                        + (p.getPatient() != null ? p.getPatient().getPatientName() : "N/A")
                        + " (Dr. " + (p.getDoctor() != null ? p.getDoctor().getName() : "N/A") + ")</html>";
                content.add(new JLabel(head));

                String[] cols = {"Medicine Name", "Quantity", "Stock", "Unit Price (RM)", "Line Total (RM)"};
                DefaultTableModel tm = new DefaultTableModel(cols, 0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }

                    @Override
                    public Class<?> getColumnClass(int c) {
                        if (c == 1 || c == 2) {
                            return Integer.class;
                        }
                        if (c == 3 || c == 4) {
                            return Double.class;
                        }
                        return String.class;
                    }
                };

                double subtotal = 0.0;
                for (PrescriptionItem it : p.getPrescriptionItems()) {
                    String medId = it.getMedicine() != null ? it.getMedicine().getMedicineId() : null;
                    String name = it.getMedicineName();
                    int qty = safeInt(it.getQuantity());
                    int stock = getStockForMedicineId(medId);
                    double unit = it.getUnitPrice();
                    double line = it.getTotalCost();
                    subtotal += line;
                    tm.addRow(new Object[]{name, qty, stock, unit, line});
                }

                JTable jt = new JTable(tm);

                DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
                    @Override
                    public void setValue(Object value) {
                        if (value instanceof Number) {
                            setText(String.format("%.2f", ((Number) value).doubleValue()));
                        } else {
                            setText("0.00");
                        }
                    }

                    @Override
                    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        setHorizontalAlignment(RIGHT);
                        return this;
                    }
                };

                jt.getColumnModel().getColumn(3).setCellRenderer(priceRenderer);
                jt.getColumnModel().getColumn(4).setCellRenderer(priceRenderer);

                jt.getTableHeader().setReorderingAllowed(false);
                JScrollPane sc = new JScrollPane(jt);
                sc.setPreferredSize(new java.awt.Dimension(620, 120));
                content.add(sc);
                content.add(new JLabel(String.format("Subtotal: RM %.2f", subtotal)));
            }

            int choice = JOptionPane.showConfirmDialog(this, content, "Confirm Dispense",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            // 4) Update stock, mark Prescription, and log dispenses
            DoublyLinkedList<DispenseRecord> newDispenses = new DoublyLinkedList<>();

            for (Pair<String, Prescription> pair : toDispense) {
                Prescription p = pair.getValue();
                for (PrescriptionItem it : p.getPrescriptionItems()) {
                    String medId = it.getMedicine() != null ? it.getMedicine().getMedicineId() : null;
                    String medName = it.getMedicineName();
                    int qty = safeInt(it.getQuantity());

                    Pair<String, Medicine> mp = findMedicinePairById(medId);
                    if (mp != null) {
                        Medicine med = mp.getValue();
                        med.setQuantity(med.getQuantity() - qty);
                    }

                    newDispenses.insertLast(new DispenseRecord(
                            p.getPrescriptionID(),
                            p.getPatient() != null ? p.getPatient().getPatientName() : "N/A",
                            p.getDoctor() != null ? p.getDoctor().getName() : "N/A",
                            medId,
                            medName,
                            qty,
                            it.getUnitPrice(),
                            it.getTotalCost(),
                            LocalDateTime.now().toString()
                    ));
                }

                p.setStatus("Dispensed");
            }

            // 5) Save dispense records first
            DoublyLinkedList<DispenseRecord> existingDispenses
                    = (DoublyLinkedList<DispenseRecord>) FileUtils.readDataFromFile("dispense");
            if (existingDispenses == null) {
                existingDispenses = new DoublyLinkedList<>();
            }
            for (DispenseRecord d : newDispenses) {
                existingDispenses.insertLast(d);
            }
            FileUtils.writeDataToFile("dispense", existingDispenses);

            JOptionPane.showMessageDialog(this, "Dispensed successfully.");
            populateFromPrescriptions();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        FileUtils.writeDataToFile("prescriptions", prescriptionList);
        FileUtils.writeDataToFile("medicine", masterMedicineList);
    }

    private Pair<String, Medicine> findMedicinePairById(String id) {
        for (Pair<String, Medicine> pair : masterMedicineList) {
            if (pair.getKey().equals(id)) {
                return pair;
            }
        }
        return null;
    }

    private class SelectCheckboxEditor extends DefaultCellEditor {

        private JCheckBox checkBox;

        public SelectCheckboxEditor() {
            super(new JCheckBox());
            checkBox = (JCheckBox) getComponent();
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            String status = (String) table.getValueAt(row, 9); // column 9 = Status
            if ("Dispensed".equals(status)) {
                JOptionPane.showMessageDialog(DispenseMedicinePanel.this,
                        "This prescription has been dispensed.",
                        "Invalid Selection",
                        JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> fireEditingCanceled()); // cancel editing
                return null; // don’t allow checkbox
            }

            checkBox.setSelected(Boolean.TRUE.equals(value));
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
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

        logoPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        formWrapperPanel = new javax.swing.JPanel();
        medicineTablePanel = new javax.swing.JScrollPane();
        medicineTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        dispenseMedicineButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(335, 125));
        setPreferredSize(new java.awt.Dimension(452, 511));
        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Dispense Medicine Entry");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        formWrapperPanel.setLayout(new java.awt.BorderLayout());

        medicineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        medicineTable.setRowHeight(24);
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTablePanel.setViewportView(medicineTable);

        formWrapperPanel.add(medicineTablePanel, java.awt.BorderLayout.CENTER);

        dispenseMedicineButton.setText("Dispense Medicine");
        dispenseMedicineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispenseMedicineButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(dispenseMedicineButton);

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(doneButton);

        formWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(formWrapperPanel, java.awt.BorderLayout.CENTER);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void dispenseMedicineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispenseMedicineButtonActionPerformed
        handleDispense();
    }//GEN-LAST:event_dispenseMedicineButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        FileUtils.writeDataToFile("medicine", masterMedicineList);
        mainFrame.showPanel("pharmacyManagement");
    }//GEN-LAST:event_doneButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton dispenseMedicineButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JPanel formWrapperPanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JTable medicineTable;
    private javax.swing.JScrollPane medicineTablePanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
