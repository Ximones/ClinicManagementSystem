package boundary.DoctorManagementUI;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MainFrame;
import enitity.Doctor;
import utility.ImageUtils;
import control.DoctorManagementController.DoctorInformationControl;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import utility.FileUtils;

/**
 *
 * @author Chok Chun Fai
 */
public class DoctorInformationPanel extends javax.swing.JPanel {

    private final MainFrame mainFrame;
    private final DoctorInformationControl control;

    /**
     * Creates new form DoctorInformationPanel
     *
     * @param mainFrame
     */
    public DoctorInformationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        DoublyLinkedList<Pair<String, Doctor>> masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        // Create the controller and pass it a reference to this view
        this.control = new DoctorInformationControl(this, mainFrame);

        loadInitialComponent();

        populateDoctorTable(control.getMasterDoctorList());

        // DocumentListener now calls the controller's filter method
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }
        });
    }
// Public method to re-read from disk and refresh the table when panel is shown

    public void reloadData() {
        DoublyLinkedList<Pair<String, Doctor>> masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (masterDoctorList == null) {
            masterDoctorList = new DoublyLinkedList<>();
        }
        populateDoctorTable(masterDoctorList);
    }

    private void loadInitialComponent() {

        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);

        // Setup for comboboxes
        sortBox.addItem("ASC");
        sortBox.addItem("DESC");
        filterBox.addItem("ID");
        filterBox.addItem("Name");
        filterBox.addItem("Position");

        // Setup for table model
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable.setModel(model);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Age");
        model.addColumn("Contact");
        model.addColumn("Position");
        model.addColumn("Status");

        // DocumentListener now calls the controller's filter method
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                control.filterDoctorList();
            }
        });
    }

    /**
     * Clears the table and populates it with a list of doctors.
     *
     * @param doctorList The list of Doctor objects to display.
     */
    public void populateDoctorTable(DoublyLinkedList<Pair<String, Doctor>> doctorList) {

        DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();
        model.setRowCount(0); // Clear existing rows

        if (doctorList == null) {
            return; // Guard against null list
        }

        // Loop through the list of doctors
        for (Pair<String, Doctor> pair : doctorList) {
            Doctor doctor = pair.getValue();

            // ** THE FIX IS HERE: Add a new empty row before setting values **
            model.addRow(new Object[]{
                pair.getKey(),
                doctor.getName(),
                doctor.getAge(),
                doctor.getPhoneNumber(),
                doctor.getPosition(),
                doctor.getStatus()
            });
        }
    }

    // Getter methods for the controller to get UI state
    public String getFilterCriterion() {
        return (String) filterBox.getSelectedItem();
    }

    public String getFilterSearchText() {
        return filterField.getText();
    }

    public String getSelectedSort() {
        return (String) sortBox.getSelectedItem();
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
        sortLabel = new javax.swing.JLabel();
        sortBox = new javax.swing.JComboBox<>();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        doctorTablePanel = new javax.swing.JScrollPane();
        doctorTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        editDoctorButton = new javax.swing.JButton();
        addDoctorButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        logoPanel.setLayout(new java.awt.BorderLayout());
        logoPanel.add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Doctor Information");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        sortLabel.setText("Sort ID By:");
        searchPanel.add(sortLabel);

        sortBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortBoxActionPerformed(evt);
            }
        });
        searchPanel.add(sortBox);

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

        doctorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        doctorTable.getTableHeader().setReorderingAllowed(false);
        doctorTablePanel.setViewportView(doctorTable);

        searchWrapperPanel.add(doctorTablePanel, java.awt.BorderLayout.CENTER);

        editDoctorButton.setText("Edit Doctor");
        editDoctorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDoctorButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(editDoctorButton);

        addDoctorButton.setText("Add Doctor");
        addDoctorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDoctorButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(addDoctorButton);

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(doneButton);

        reportButton.setText("Generate Report");
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });
        ButtonPanel.add(reportButton);

        searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

        titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

        logoPanel.add(titlePanel, java.awt.BorderLayout.CENTER);

        add(logoPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addDoctorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDoctorButtonActionPerformed
        control.addNewDoctor();
    }//GEN-LAST:event_addDoctorButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        control.Exit();
    }//GEN-LAST:event_doneButtonActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        filterField.setText("");
    }//GEN-LAST:event_filterBoxActionPerformed

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportButtonActionPerformed
        control.generateReports();
    }//GEN-LAST:event_reportButtonActionPerformed

    private void sortBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBoxActionPerformed
        control.sortDoctorList();
    }//GEN-LAST:event_sortBoxActionPerformed

    private void editDoctorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDoctorButtonActionPerformed
        control.editDoctor();
    }//GEN-LAST:event_editDoctorButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton addDoctorButton;
    private javax.swing.JTable doctorTable;
    private javax.swing.JScrollPane doctorTablePanel;
    private javax.swing.JButton doneButton;
    private javax.swing.JButton editDoctorButton;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JButton reportButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JComboBox<String> sortBox;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
