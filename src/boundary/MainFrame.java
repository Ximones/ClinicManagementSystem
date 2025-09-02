package boundary;

import control.ConsultationController.ConsultationControl;
import boundary.DoctorManagementUI.DoctorInformationPanel;
import boundary.DoctorManagementUI.DoctorManagementPanel;
import boundary.DoctorManagementUI.DoctorSchedulePanel;
import boundary.PharmacyManagementUI.PharmacyManagementPanel;
import boundary.PatientManagementUI.PatientManagementPanel;
import boundary.PharmacyManagementUI.MedicineInformationPanel;
import boundary.PatientManagementUI.PatientRegistrationPanel;
import boundary.PatientManagementUI.QueueManagementPanel;
import boundary.ConsultationManagementUI.ConsultationManagementPanel;
import boundary.ConsultationManagementUI.ConsultationPanel;
import boundary.ConsultationManagementUI.AppointmentPanel;
import boundary.ConsultationManagementUI.PrescriptionPanel;
import boundary.ConsultationManagementUI.QueuePanel;
import boundary.ConsultationManagementUI.ConsultationReportsPanel;
import boundary.ConsultationManagementUI.ConsultationHistoryPanel;
import boundary.PharmacyManagementUI.DispenseMedicinePanel;
import boundary.MedicalTreatmentManagementUI.MedicalTreatmentManagementPanel;
import boundary.MedicalTreatmentManagementUI.DiagnosisEntryPanel;
import boundary.MedicalTreatmentManagementUI.TreatmentHistoryPanel;
import boundary.PharmacyManagementUI.DispenseHistoryPanel;
import java.awt.CardLayout;

/**
 *
 * @author Chok Chun Fai, Lee Wan Ching , Lim Sze Ping , Liow Zhen Bang , Tan Jun Yew
 */
public class MainFrame extends javax.swing.JFrame {

    private MedicineInformationPanel medInfoPanel;
    private DispenseMedicinePanel dispenseMedPanel;
    private DispenseHistoryPanel dispenseHistoryPanel;
    private PrescriptionPanel prescriptionPanel;
    private QueuePanel queuePanel; // Reference to queue panel
    private ConsultationPanel consultationPanel; // Reference to consultation panel
    private ConsultationControl consultationControl; // Shared control
    private DiagnosisEntryPanel diagnosisPanel; // Add this instance variable

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();

        /* Create instances of multiple screen panels
        * Pass 'this' Main Frame 
         */
        consultationControl = new ConsultationControl();
        ClinicMenuPanel clinicPanel = new ClinicMenuPanel(this);
        DoctorManagementPanel docPanel = new DoctorManagementPanel(this);
        DoctorInformationPanel docInfoPanel = new DoctorInformationPanel(this);
        DoctorSchedulePanel docSchedulePanel = new DoctorSchedulePanel(this);

        PharmacyManagementPanel pharPanel = new PharmacyManagementPanel(this);
        medInfoPanel = new MedicineInformationPanel(this);
        dispenseMedPanel = new DispenseMedicinePanel(this);
        dispenseHistoryPanel = new DispenseHistoryPanel(this);

        PatientManagementPanel patientPanel = new PatientManagementPanel(this);
        PatientRegistrationPanel patRegPanel = new PatientRegistrationPanel(this);
        QueueManagementPanel queManagementPanel = new QueueManagementPanel(this);

        // Consultation Management Panels
        ConsultationManagementPanel consultationManagementPanel = new ConsultationManagementPanel(this);
        queuePanel = new QueuePanel(this, consultationControl);
        consultationPanel = new ConsultationPanel(this, queuePanel, consultationControl);

        // Set the consultation panel reference in the queue panel
        queuePanel.setConsultationPanel(consultationPanel);

        AppointmentPanel appointmentPanel = new AppointmentPanel(this, consultationControl);
        prescriptionPanel = new PrescriptionPanel(this);
        ConsultationReportsPanel consultationReportsPanel = new ConsultationReportsPanel(this, consultationControl);
        ConsultationHistoryPanel consultationHistoryPanel = new ConsultationHistoryPanel(this, consultationControl);

        // Medical Treatment Management Panels
        MedicalTreatmentManagementPanel medicalPanel = new MedicalTreatmentManagementPanel(this);
        DiagnosisEntryPanel diagnosisPanel = new DiagnosisEntryPanel(this);
        TreatmentHistoryPanel historyPanel = new TreatmentHistoryPanel(this);

        // Add the panels to the cardPanel with unique names
        cardPanel.add(clinicPanel, "clinicMenu");
        cardPanel.add(docPanel, "doctorManagement");
        cardPanel.add(docInfoPanel, "doctorInformation");
        cardPanel.add(docSchedulePanel, "doctorSchedule");

        cardPanel.add(pharPanel, "pharmacyManagement");
        cardPanel.add(medInfoPanel, "medicineInformation");
        cardPanel.add(dispenseMedPanel, "dispenseMedicine");
        cardPanel.add(dispenseHistoryPanel, "dispenseHistory");

        cardPanel.add(patientPanel, "patientManagement");
        cardPanel.add(patRegPanel, "patientRegistration");
        cardPanel.add(queManagementPanel, "queueManagement");

        // Add consultation management panels
        cardPanel.add(consultationManagementPanel, "consultationManagement");
        cardPanel.add(consultationPanel, "consultationPanel");
        cardPanel.add(appointmentPanel, "appointmentPanel");
        cardPanel.add(prescriptionPanel, "prescriptionPanel");
        cardPanel.add(queuePanel, "queuePanel");
        cardPanel.add(consultationReportsPanel, "consultationReports");
        cardPanel.add(consultationHistoryPanel, "consultationHistory");

        cardPanel.add(medicalPanel, "medicalManagement");
        cardPanel.add(diagnosisPanel, "diagnosisEntry");
        cardPanel.add(historyPanel, "treatmentHistory");

        // Set frame properties
        this.setTitle("Clinic Management System");
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public MedicineInformationPanel getMedicineInformationPanel() {
        return medInfoPanel;
    }

    public DispenseMedicinePanel getDispenseMedicinePanel() {
        return dispenseMedPanel;
    }

    public DispenseHistoryPanel getDispenseHistoryPanel() {
        return dispenseHistoryPanel;
    }

    public PrescriptionPanel getPrescriptionPanel() {
        return prescriptionPanel;
    }

    public QueuePanel getQueuePanel() {
        return queuePanel;
    }

    public ConsultationPanel getConsultationPanel() {
        return consultationPanel;
    }

    public DiagnosisEntryPanel getDiagnosisEntryPanel() {
        return diagnosisPanel;
    }

    // Function to switch different windows
    public void showPanel(String panelName) {
        // Targeted refresh based on known panel names
        switch (panelName) {
            case "medicineInformation":
                if (medInfoPanel != null) {
                    medInfoPanel.reloadData();
                }
                break;
            case "doctorInformation": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.DoctorManagementUI.DoctorInformationPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.DoctorManagementUI.DoctorInformationPanel) {
                    ((boundary.DoctorManagementUI.DoctorInformationPanel) comp).reloadData();
                }
                break;
            }
            case "patientRegistration": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.PatientManagementUI.PatientRegistrationPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.PatientManagementUI.PatientRegistrationPanel) {
                    ((boundary.PatientManagementUI.PatientRegistrationPanel) comp).reloadData();
                }
                break;
            }
            case "prescriptionPanel": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.PrescriptionPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.PrescriptionPanel) {
                    ((boundary.ConsultationManagementUI.PrescriptionPanel) comp).reloadData();
                }
                break;
            }
            case "queuePanel": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.QueuePanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.QueuePanel) {
                    ((boundary.ConsultationManagementUI.QueuePanel) comp).refreshQueueDisplay();
                }
                break;
            }
            case "consultationHistory": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.ConsultationHistoryPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.ConsultationHistoryPanel) {
                    ((boundary.ConsultationManagementUI.ConsultationHistoryPanel) comp).reloadData();
                }
                break;
            }
            case "consultationPanel": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.ConsultationPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.ConsultationPanel) {
                    ((boundary.ConsultationManagementUI.ConsultationPanel) comp).reloadData();
                }
                break;
            }
            case "appointmentPanel": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.AppointmentPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.AppointmentPanel) {
                    ((boundary.ConsultationManagementUI.AppointmentPanel) comp).reloadData();
                }
                break;
            }
            case "consultationReports": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.ConsultationManagementUI.ConsultationReportsPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.ConsultationManagementUI.ConsultationReportsPanel) {
                    ((boundary.ConsultationManagementUI.ConsultationReportsPanel) comp).reloadData();
                }
                break;
            }
            case "treatmentHistory": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.MedicalTreatmentManagementUI.TreatmentHistoryPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.MedicalTreatmentManagementUI.TreatmentHistoryPanel) {
                    ((boundary.MedicalTreatmentManagementUI.TreatmentHistoryPanel) comp).onPanelShow();
                }
                break;
            }
            case "diagnosisEntry": {
                java.awt.Component comp = null;
                for (java.awt.Component c : cardPanel.getComponents()) {
                    if (c instanceof boundary.MedicalTreatmentManagementUI.DiagnosisEntryPanel) {
                        comp = c;
                        break;
                    }
                }
                if (comp instanceof boundary.MedicalTreatmentManagementUI.DiagnosisEntryPanel) {
                    ((boundary.MedicalTreatmentManagementUI.DiagnosisEntryPanel) comp).onPanelShow();
                }
                break;
            }
        }

        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, panelName);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cardPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));

        cardPanel.setMaximumSize(new java.awt.Dimension(700, 500));
        cardPanel.setMinimumSize(new java.awt.Dimension(700, 500));
        cardPanel.setName(""); // NOI18N
        cardPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        cardPanel.setLayout(new java.awt.CardLayout());
        getContentPane().add(cardPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    // End of variables declaration//GEN-END:variables
}
