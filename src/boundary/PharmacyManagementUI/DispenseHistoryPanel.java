package boundary.PharmacyManagementUI;

import adt.DoublyLinkedList;
import boundary.MainFrame;
import enitity.DispenseRecord;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import utility.FileUtils;
import utility.ImageUtils;
import utility.ReportGenerator;

/**
 *
 * @author Lee Wan Ching
 */
public class DispenseHistoryPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private DoublyLinkedList<DispenseRecord> masterDispenseList;

    /**
     * Creates new form DispenseHistoryPanel
     */
    public DispenseHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        loadInitialComponent();
        loadMedicineTable();
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
    }

    private void loadInitialComponent() {
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTable.setRowHeight(24);
        medicineTable.setAutoCreateRowSorter(false);

        filterBox.addItem("Medicine ID");
        filterBox.addItem("Medicine Name");
        filterBox.addItem("Dispense Date");
    }

    public void loadMedicineTable() {
        // Read data from file
        masterDispenseList = (DoublyLinkedList<DispenseRecord>) FileUtils.readDataFromFile("dispense");

        if (masterDispenseList != null && !masterDispenseList.isEmpty()) {
            masterDispenseList.sort(); // Uses compareTo from DispenseRecord
        }

        populateMedicineTable(masterDispenseList);
    }

    private void populateMedicineTable(DoublyLinkedList<DispenseRecord> list) {
        String[] columnNames = {
            "Prescription ID", "Patient Name", "Doctor Name", "Medicine ID",
            "Medicine Name", "Quantity", "Unit Price (RM)", "Line Total (RM)", "Dispense Date"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (list != null && !list.isEmpty()) {
            for (DispenseRecord record : list) {
                Object[] rowData = {
                    record.getPrescriptionId(),
                    record.getPatientName(),
                    record.getDoctorName(),
                    record.getMedicineId(),
                    record.getMedicineName(),
                    record.getQuantity(),
                    String.format("%.2f", record.getUnitPrice()),
                    String.format("%.2f", record.getLineTotal()),
                    formatDate(record.getDateTime())
                };
                model.addRow(rowData);
            }
        }

        medicineTable.setModel(model);
    }

    private void filterTable() {
        if (masterDispenseList == null || masterDispenseList.isEmpty()) {
            return;
        }

        String criterion = (String) filterBox.getSelectedItem();
        String searchText = filterField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            populateMedicineTable(masterDispenseList); // show all
            return;
        }

        DoublyLinkedList<DispenseRecord> filteredList = new DoublyLinkedList<>();
        for (DispenseRecord record : masterDispenseList) {
            String value = "";
            switch (criterion) {
                case "Medicine ID":
                    value = record.getMedicineId();
                    break;
                case "Medicine Name":
                    value = record.getMedicineName();
                    break;
                case "Dispense Date":
                    value = formatDate(record.getDateTime());
                    break;
            }
            if (value != null && value.toLowerCase().contains(searchText)) {
                filteredList.insertLast(record);
            }
        }

        populateMedicineTable(filteredList);
    }

    private String formatDate(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }
        try {
            java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(dateTime);
            java.time.format.DateTimeFormatter formatter
                    = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return ldt.format(formatter);
        } catch (Exception e) {
            return dateTime;
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
        searchPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        medicineTablePanel = new javax.swing.JScrollPane();
        medicineTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        dispenseLogButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Medicine Dispensed History");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        formWrapperPanel.setLayout(new java.awt.BorderLayout());

        filterLabel.setText("Filter By :");
        searchPanel.add(filterLabel);

        filterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBoxActionPerformed(evt);
            }
        });
        searchPanel.add(filterBox);

        filterField.setColumns(15);
        filterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterFieldActionPerformed(evt);
            }
        });
        searchPanel.add(filterField);

        formWrapperPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

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

        dispenseLogButton.setText("Generate Daily Dispensing Report");
        dispenseLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispenseLogButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(dispenseLogButton);

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

    private void dispenseLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispenseLogButtonActionPerformed
        DoublyLinkedList<DispenseRecord> dispenseRecords = (DoublyLinkedList<DispenseRecord>) FileUtils.readDataFromFile("dispense");

        if (dispenseRecords == null || dispenseRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No dispensing records found for today.", "Report Generation Failed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ReportGenerator.generateDailyDispenseReport(dispenseRecords);

        JOptionPane.showMessageDialog(this, "Daily dispensing log report has been generated.", "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_dispenseLogButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        mainFrame.showPanel("pharmacyManagement");
    }//GEN-LAST:event_doneButtonActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed

    }//GEN-LAST:event_filterBoxActionPerformed

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed

    }//GEN-LAST:event_filterFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton dispenseLogButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JPanel formWrapperPanel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JTable medicineTable;
    private javax.swing.JScrollPane medicineTablePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
