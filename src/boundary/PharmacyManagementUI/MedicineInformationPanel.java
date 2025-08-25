package boundary.PharmacyManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.Medicine;
import java.text.DecimalFormat;
import utility.FileUtils;
import utility.ImageUtils;
import utility.ReportGenerator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lee Wan Ching
 */
public class MedicineInformationPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private DoublyLinkedList<Pair<String, Medicine>> masterMedicineList;

    public MedicineInformationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();

        loadInitialComponent();
        loadInitialData();
        populateMedicineTable(masterMedicineList);

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

        medicineTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                String medicineId = (String) medicineTable.getValueAt(row, 0);
                Pair<String, Medicine> targetPair = findMedicinePairById(medicineId);

                if (targetPair != null) {
                    Medicine medicine = targetPair.getValue();
                    Object newValue = medicineTable.getValueAt(row, column);

                    try {
                        switch (column) {
                            case 0:
                                medicine.setMedicineId(newValue.toString());
                                break;
                            case 1:
                                medicine.setName(newValue.toString());
                                break;
                            case 2:
                                medicine.setBrandName(newValue.toString());
                                break;
                            case 3:
                                medicine.setCategory(newValue.toString());
                                break;
                            case 4:
                                medicine.setFormulation(newValue.toString());
                                break;
                            case 5:
                                medicine.setDosageForm(newValue.toString());
                                break;
                            case 6:
                                int quantity = Integer.parseInt(newValue.toString());
                                if (quantity < 0) {
                                    throw new NumberFormatException();
                                }
                                medicine.setQuantity(quantity);
                                break;
                            case 7:
                                double price = Double.parseDouble(newValue.toString());
                                if (price < 0) {
                                    throw new NumberFormatException();
                                }
                                medicine.setPrice(price);
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Invalid input! Please enter a valid number.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                        populateMedicineTable(masterMedicineList);
                    }
                }
            }
        });
    }

    private void loadInitialComponent() {
        logoLabel = ImageUtils.getImageLabel("pharmacy_logo.png", logoLabel);

        DoublyLinkedList<String> filterCriteria = new DoublyLinkedList<>();
        filterCriteria.insertLast("ID");
        filterCriteria.insertLast("Name");
        filterCriteria.insertLast("Brand Name");
        filterCriteria.insertLast("Category");
        filterCriteria.insertLast("Formulation");
        filterCriteria.insertLast("Dosage");
        filterCriteria.insertLast("Quantity");
        filterCriteria.insertLast("Price");

        for (String criterion : filterCriteria) {
            filterBox.addItem(criterion);
        }

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 6:
                        return Integer.class; // quantity
                    case 7:
                        return Double.class;  // price
                    default:
                        return String.class;
                }
            }
        };

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Brand Name");
        model.addColumn("Category");
        model.addColumn("Formulation");
        model.addColumn("Dosage");
        model.addColumn("Quantity");
        model.addColumn("Price (RM)");

        medicineTable.setModel(model);
        medicineTable.setAutoCreateRowSorter(true);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Double) {
                    setText(decimalFormat.format(value));
                } else {
                    super.setValue(value);
                }
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        medicineTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        // Category ComboBox editor
        String[] categories = {
            "Antibiotic", "Analgesic", "Antipyretic", "Antihistamine", "Antiviral",
            "Antifungal", "Antidepressant", "Cardiovascular", "Respiratory",
            "Gastrointestinal", "Dermatological", "Vitamin / Supplement", "Other"
        };
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        medicineTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(categoryComboBox));

        // Formulation ComboBox editor
        String[] formulations = {
            "Tablet", "Capsule", "Liquid", "Syrup", "Ointment", "Cream", "Gel",
            "Injection", "Inhaler", "Drops", "Suppository", "Powder", "Patch", "Spray"
        };
        JComboBox<String> formulationComboBox = new JComboBox<>(formulations);
        medicineTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(formulationComboBox));

    }

    private void loadInitialData() {
//        InsertMedicineData();
                
        DoublyLinkedList<Pair<String, Medicine>> medicineList
                = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");

        if (!medicineList.isEmpty()) {
            Medicine.setMedicineIndex(medicineList.getSize());
        }

        masterMedicineList = medicineList;
    }

    private void populateMedicineTable(DoublyLinkedList<Pair<String, Medicine>> list) {
        DefaultTableModel model = (DefaultTableModel) medicineTable.getModel();
        model.setRowCount(0);

        for (Pair<String, Medicine> pair : list) {
            Medicine med = pair.getValue();
            model.addRow(new Object[]{
                pair.getKey(),
                med.getName(),
                med.getBrandName(),
                med.getCategory(),
                med.getFormulation(),
                med.getDosageForm(),
                med.getQuantity(),
                med.getPrice()
            });
        }

    }

    private void filterTable() {
        String selectedCriterion = (String) filterBox.getSelectedItem();
        String searchText = filterField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            populateMedicineTable(masterMedicineList);
            return;
        }

        DoublyLinkedList<Pair<String, Medicine>> searchResults = new DoublyLinkedList<>();

        for (Pair<String, Medicine> pair : masterMedicineList) {
            Medicine med = pair.getValue();
            String id = pair.getKey();
            boolean match = false;

            switch (selectedCriterion) {
                case "ID":
                    match = id.toLowerCase().contains(searchText);
                    break;
                case "Name":
                    match = med.getName().toLowerCase().contains(searchText);
                    break;
                case "Brand Name":
                    match = med.getBrandName().toLowerCase().contains(searchText);
                    break;
                case "Category":
                    match = med.getCategory().toLowerCase().contains(searchText);
                    break;
                case "Formulation":
                    match = med.getFormulation().toLowerCase().contains(searchText);
                    break;
                case "Dosage Form":
                    match = med.getDosageForm().toLowerCase().contains(searchText);
                    break;
                case "Quantity":
                    match = Integer.toString(med.getQuantity()).contains(searchText);
                    break;
                case "Price":
                    match = String.format("%.2f", med.getPrice()).contains(searchText);
                    break;
            }

            if (match) {
                searchResults.insertLast(pair);
            }
        }

        populateMedicineTable(searchResults);
    }

    private Pair<String, Medicine> findMedicinePairById(String id) {
        if (id == null || masterMedicineList == null) {
            return null;
        }

        for (Pair<String, Medicine> pair : masterMedicineList) {
            if (id.equals(pair.getKey())) {
                return pair;
            }
        }

        return null;
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
        searchWrapperPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        medicineTablePanel = new javax.swing.JScrollPane();
        medicineTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        addMedicineButton = new javax.swing.JButton();
        generateLowStockReportButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Medicine Information");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

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

        searchWrapperPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        medicineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTablePanel.setViewportView(medicineTable);

        searchWrapperPanel.add(medicineTablePanel, java.awt.BorderLayout.CENTER);

        addMedicineButton.setText("Add Medicine");
        addMedicineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMedicineButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(addMedicineButton);

        generateLowStockReportButton.setText("Generate Low Stock Report");
        generateLowStockReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLowStockReportButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(generateLowStockReportButton);

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(doneButton);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed

    private void addMedicineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMedicineButtonActionPerformed
        MedicineDialog dialog = new MedicineDialog(mainFrame, true);
        dialog.setVisible(true);

        Pair<String, Medicine> newMedicinePair = dialog.getResult();
        if (newMedicinePair != null) {
            String newMedicineName = newMedicinePair.getValue().getName();
            boolean nameExists = false;

            for (Pair<String, Medicine> pair : masterMedicineList) {
                if (pair.getValue().getName().equalsIgnoreCase(newMedicineName)) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists) {
                JOptionPane.showMessageDialog(this,
                        "A medicine with this name already exists.",
                        "Duplicate Medicine Name",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                masterMedicineList.insertLast(newMedicinePair);
                populateMedicineTable(masterMedicineList);
            }
        }
    }//GEN-LAST:event_addMedicineButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        FileUtils.writeDataToFile("medicine", masterMedicineList);
        mainFrame.showPanel("pharmacyManagement");
    }//GEN-LAST:event_doneButtonActionPerformed

    private void generateLowStockReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLowStockReportButtonActionPerformed
        ReportGenerator.generateLowStockReport(masterMedicineList, 50);
        JOptionPane.showMessageDialog(this, "Low Stock Report generated successfully!");
    }//GEN-LAST:event_generateLowStockReportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton addMedicineButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JButton generateLowStockReportButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JTable medicineTable;
    private javax.swing.JScrollPane medicineTablePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
