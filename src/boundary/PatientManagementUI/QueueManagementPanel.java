/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package boundary.PatientManagementUI;

import adt.DoublyLinkedList;
import adt.Node;
import adt.Pair;
import boundary.MainFrame;
import enitity.Patient;
import enitity.QueueEntry;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utility.ImageUtils;
import utility.ReportGenerator;

/**
 *
 * @author szepi
 */
public class QueueManagementPanel extends javax.swing.JPanel {

     private MainFrame mainFrame;
     private DoublyLinkedList<QueueEntry> queueList = new DoublyLinkedList<>();
    /**
     * Creates new form QueueManagementPanel
     */
    public QueueManagementPanel(MainFrame mainFrame) {
         this.mainFrame = mainFrame;
        initComponents();
        
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
        loadQueueData();
        displayQueueData();
        
         filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            filterQueueTable();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            filterQueueTable();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            filterQueueTable();
        }
    });

    // Add listener to filterBox to update filter when selection changes
    filterBox.addActionListener(evt -> filterQueueTable());
    }

    private int lastNumber = 1000; // starting number

    private String generateQueueNumber() {
    lastNumber++;
    return "E" + lastNumber;
    }
    
    private void loadQueueData() {
    try {
        // Try to load in new format (Pair<String, QueueEntry>)
        DoublyLinkedList<Pair<String, QueueEntry>> savedQueuePair =
            (DoublyLinkedList<Pair<String, QueueEntry>>) utility.FileUtils.readDataFromFile("queue");
        if (savedQueuePair != null && !savedQueuePair.isEmpty()) {
            // Convert Pair format to QueueEntry format for this panel
            queueList = new DoublyLinkedList<>();
            for (Pair<String, QueueEntry> pair : savedQueuePair) {
                queueList.insertLast(pair.getValue());
                // Update lastNumber based on existing queue numbers
                String qNo = pair.getValue().getQueueNumber(); // e.g., "E1010"
                try {
                    int num = Integer.parseInt(qNo.substring(1)); // get 1010
                    if (num > lastNumber) {
                        lastNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        } else {
            queueList = new DoublyLinkedList<>();
        }
    } catch (ClassCastException e) {
        // Handle old format (direct QueueEntry objects)
        try {
            DoublyLinkedList<QueueEntry> savedQueue =
                (DoublyLinkedList<QueueEntry>) utility.FileUtils.readDataFromFile("queue");
            if (savedQueue != null) {
                queueList = savedQueue;
                // update lastNumber
                for (QueueEntry entry : savedQueue) {
                    String qNo = entry.getQueueNumber();
                    try {
                        int num = Integer.parseInt(qNo.substring(1));
                        if (num > lastNumber) lastNumber = num;
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                queueList = new DoublyLinkedList<>();
            }
        } catch (Exception ex) {
            System.err.println("Error loading queue data: " + ex.getMessage());
            queueList = new DoublyLinkedList<>();
        }
    }
}


    
    private void saveQueueData() {
    // Convert QueueEntry format to Pair format for saving to maintain consistency
    DoublyLinkedList<Pair<String, QueueEntry>> pairQueueList = new DoublyLinkedList<>();
    for (QueueEntry entry : queueList) {
        Pair<String, QueueEntry> pair = new Pair<>(entry.getQueueNumber(), entry);
        pairQueueList.insertLast(pair);
    }
    utility.FileUtils.writeDataToFile("queue", pairQueueList);
}
    
private void displayQueueData() {
    DefaultTableModel model = (DefaultTableModel) queueTable.getModel();

    // Clear existing table data
    model.setRowCount(0);

    // Set column identifiers if not already set
    if (model.getColumnCount() == 0) {
        model.setColumnIdentifiers(new String[] {
            "Patient ID", "Patient Name", "IC No", 
            "Queue No", "Status", 
            "Enqueue Time", "Start Time",
            "Waiting Time"
        });
    }

    // Loop through queueList and add rows to the table
    for (QueueEntry entry : queueList) {
        Patient patient = entry.getPatient();
        model.addRow(new Object[] {
            patient.getPatientID(),
            patient.getPatientName(),
            patient.getPatientIC(),
            entry.getQueueNumber(),
            entry.getStatus(),
            entry.getFormattedEnqueueTime(),
            entry.getFormattedStartTime(),
            entry.getFormattedWaitingTime()
        });
    }
}


private void filterQueueTable() {
    String keyword = filterField.getText().trim().toLowerCase();
    String filterBy = filterBox.getSelectedItem().toString();

    DefaultTableModel model = (DefaultTableModel) queueTable.getModel();
    model.setRowCount(0); // clear table

    for (QueueEntry entry : queueList) {
        Patient p = entry.getPatient();
        boolean matches = false;

        switch (filterBy) {
            case "Queue No":
                matches = entry.getQueueNumber().toLowerCase().contains(keyword);
                break;
            case "Status":
                matches = entry.getStatus().toLowerCase().contains(keyword);
                break;
        }

        if (matches) {
            model.addRow(new Object[]{
                p.getPatientID(),
                p.getPatientName(),
                p.getPatientIC(),
                entry.getQueueNumber(),
                entry.getStatus(),
                entry.getFormattedEnqueueTime(),
                entry.getFormattedStartTime(),
                entry.getFormattedWaitingTime()
            });
        }
    }
}



public void reloadData() {
    loadQueueData();      // re-read from file
    displayQueueData();   // update the table
}

public void setVisible(boolean aFlag) {
    super.setVisible(aFlag);
    if (aFlag) {
        reloadData();   // refresh every time this panel is displayed
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
        sortLabel = new javax.swing.JLabel();
        sortBox = new javax.swing.JComboBox<>();
        filterLabel = new javax.swing.JLabel();
        filterBox = new javax.swing.JComboBox<>();
        filterField = new javax.swing.JTextField();
        queueTablePanel = new javax.swing.JScrollPane();
        queueTable = new javax.swing.JTable();
        ButtonPanel = new javax.swing.JPanel();
        AddPatientQueue = new javax.swing.JButton();
        GenerateReport = new javax.swing.JButton();
        Done = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Queue Management");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        searchWrapperPanel.setLayout(new java.awt.BorderLayout());

        sortLabel.setText("Sort Queue No by:");
        searchPanel.add(sortLabel);

        sortBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ASC", "DESC"}));
        sortBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortBoxActionPerformed(evt);
            }
        });
        searchPanel.add(sortBox);

        filterLabel.setText("Filter By :");
        searchPanel.add(filterLabel);

        filterBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Queue No", "Status"}));
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

        queueTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {}
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }
    );
    queueTablePanel.setViewportView(queueTable);

    searchWrapperPanel.add(queueTablePanel, java.awt.BorderLayout.CENTER);

    AddPatientQueue.setText("Add Patient Queue");
    AddPatientQueue.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            AddPatientQueueActionPerformed(evt);
        }
    });
    ButtonPanel.add(AddPatientQueue);

    GenerateReport.setText("Generate Report");
    GenerateReport.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            GenerateReportActionPerformed(evt);
        }
    });
    ButtonPanel.add(GenerateReport);

    Done.setText("Done");
    Done.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            DoneActionPerformed(evt);
        }
    });
    ButtonPanel.add(Done);

    searchWrapperPanel.add(ButtonPanel, java.awt.BorderLayout.PAGE_END);

    titlePanel.add(searchWrapperPanel, java.awt.BorderLayout.CENTER);

    add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed

    private void AddPatientQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPatientQueueActionPerformed
       QueueDialog dialog = new QueueDialog(mainFrame, true);
       dialog.setLocationRelativeTo(this);
       dialog.setVisible(true);

       Patient selectedPatient = dialog.getResult();

if (selectedPatient != null) {
    String queueNo = generateQueueNumber();
    String status = "Waiting";

    // Create QueueEntry object
    QueueEntry entry = new QueueEntry(selectedPatient, queueNo, status);
    queueList.insertLast(entry);
    saveQueueData(); // save to file

    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) queueTable.getModel();
    if (model.getColumnCount() == 0) {
        model.setColumnIdentifiers(new String[] {
            "Patient ID", "Patient Name", "IC No", "Queue No", "Status"
        });
    }

    model.addRow(new Object[] {
        selectedPatient.getPatientID(),
        selectedPatient.getPatientName(),
        selectedPatient.getPatientIC(),
        queueNo,
        status
    });
}

    }//GEN-LAST:event_AddPatientQueueActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    private void DoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneActionPerformed
        JOptionPane.showMessageDialog(this, "Queue updates applied!");
        mainFrame.showPanel("patientManagement");
    }//GEN-LAST:event_DoneActionPerformed

    private void GenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateReportActionPerformed
        ReportGenerator.generateQueueStatisticsReport(queueList);
         JOptionPane.showMessageDialog(this, "Queue Statistics Report generated successfully!");
    }//GEN-LAST:event_GenerateReportActionPerformed

    private void sortBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBoxActionPerformed

        if (queueList == null || queueList.isEmpty()) {
        return; // nothing to sort
    }

    String selectedSort = (String) sortBox.getSelectedItem();

    // Convert DoublyLinkedList to ArrayList for sorting
    java.util.List<QueueEntry> list = new java.util.ArrayList<>();
    for (QueueEntry entry : queueList) {
        list.add(entry);
    }

    // Sort by Queue Number (ascending)
    list.sort(null); // uses compareTo in QueueEntry

    // Reverse if DESC selected
    if ("DESC".equalsIgnoreCase(selectedSort)) {
        java.util.Collections.reverse(list);
    }

    // Update queueList with sorted entries
    queueList = new adt.DoublyLinkedList<>();
    for (QueueEntry entry : list) {
        queueList.insertLast(entry);
    }

    // Refresh table
    displayQueueData();
    }//GEN-LAST:event_sortBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddPatientQueue;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JButton Done;
    private javax.swing.JButton GenerateReport;
    private javax.swing.JComboBox<String> filterBox;
    private javax.swing.JTextField filterField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTable queueTable;
    private javax.swing.JScrollPane queueTablePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchWrapperPanel;
    private javax.swing.JComboBox<String> sortBox;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
