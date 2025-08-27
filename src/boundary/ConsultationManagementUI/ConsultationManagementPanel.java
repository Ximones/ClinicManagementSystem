package boundary.ConsultationManagementUI;

import boundary.MainFrame;
import utility.ImageUtils;

/**
 * Main Consultation Management Panel (reset)
 */
public class ConsultationManagementPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;

    public ConsultationManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        logoLabel = ImageUtils.getImageLabel("tarumt_logo.png", logoLabel);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        buttonWrapperPanel = new javax.swing.JPanel();
        buttonWrapper = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        consultationButton = new javax.swing.JButton();
        appointmentButton = new javax.swing.JButton();
        queueButton = new javax.swing.JButton();
        reportsButton = new javax.swing.JButton();
        prescriptionButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        consultationHistoryButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        add(logoLabel, java.awt.BorderLayout.PAGE_START);

        titlePanel.setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" Consultation Management Menu");
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titlePanel.add(titleLabel, java.awt.BorderLayout.PAGE_START);

        buttonWrapperPanel.setLayout(new java.awt.BorderLayout());

        buttonPanel.setMinimumSize(new java.awt.Dimension(1000, 800));
        buttonPanel.setLayout(new java.awt.GridLayout(4, 2, 50, 50));

        consultationButton.setText("Manage Consultations");
        consultationButton.setPreferredSize(new java.awt.Dimension(200, 50));
        consultationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultationButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(consultationButton);

        appointmentButton.setText("Manage Appointments");
        appointmentButton.setPreferredSize(new java.awt.Dimension(200, 50));
        appointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(appointmentButton);

        queueButton.setText("Patient Queue");
        queueButton.setPreferredSize(new java.awt.Dimension(200, 50));
        queueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queueButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(queueButton);

        reportsButton.setText("Generate Reports");
        reportsButton.setPreferredSize(new java.awt.Dimension(200, 50));
        reportsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportsButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(reportsButton);

        prescriptionButton.setText("Manage Prescriptions");
        prescriptionButton.setPreferredSize(new java.awt.Dimension(200, 50));
        prescriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prescriptionButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(prescriptionButton);

        consultationHistoryButton.setText("Consultation History");
        consultationHistoryButton.setPreferredSize(new java.awt.Dimension(200, 50));
        consultationHistoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultationHistoryButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(consultationHistoryButton);

        backButton.setText("Main Menu");
        backButton.setPreferredSize(new java.awt.Dimension(200, 50));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(backButton);

        buttonWrapper.add(buttonPanel);

        buttonWrapperPanel.add(buttonWrapper, java.awt.BorderLayout.CENTER);

        titlePanel.add(buttonWrapperPanel, java.awt.BorderLayout.CENTER);

        add(titlePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void consultationButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("consultationPanel");
    }

    private void appointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("appointmentPanel");
    }

    private void prescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("prescriptionPanel");
    }

    private void queueButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("queuePanel");
    }

    private void reportsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("consultationReports");
    }

    private void consultationHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("consultationHistory");
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        mainFrame.showPanel("clinicMenu");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appointmentButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel buttonWrapper;
    private javax.swing.JPanel buttonWrapperPanel;
    private javax.swing.JButton consultationButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton prescriptionButton;
    private javax.swing.JButton queueButton;
    private javax.swing.JButton reportsButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JButton consultationHistoryButton;
    // End of variables declaration//GEN-END:variables
} 
