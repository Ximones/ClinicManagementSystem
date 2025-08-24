/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Prescription entity class for managing prescriptions linked to consultations
 * @author Zhen Bang
 */
public class Prescription implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static int prescriptionIndex = 0;
    
    private String prescriptionID;
    private Consultation consultation;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime prescriptionDate;
    private String diagnosis;
    private List<PrescriptionItem> prescriptionItems;
    private String instructions;
    private String status; // "Active", "Completed", "Cancelled"
    private LocalDateTime validUntil;
    
    public Prescription() {
        this.prescriptionID = generatePrescriptionID();
        this.prescriptionItems = new ArrayList<>();
        this.status = "Active";
        this.prescriptionDate = LocalDateTime.now();
    }
    
    public Prescription(Consultation consultation, String diagnosis, String instructions) {
        this.prescriptionID = generatePrescriptionID();
        this.consultation = consultation;
        this.patient = consultation.getPatient();
        this.doctor = consultation.getDoctor();
        this.diagnosis = diagnosis;
        this.instructions = instructions;
        this.prescriptionItems = new ArrayList<>();
        this.status = "Active";
        this.prescriptionDate = LocalDateTime.now();
        this.validUntil = LocalDateTime.now().plusDays(30); // Default 30 days validity
    }
    
    private static String generatePrescriptionID() {
        prescriptionIndex++;
        return String.format("PR%03d", prescriptionIndex);
    }
    
    // Getters and Setters
    public String getPrescriptionID() {
        return prescriptionID;
    }
    
    public void setPrescriptionID(String prescriptionID) {
        this.prescriptionID = prescriptionID;
    }
    
    public Consultation getConsultation() {
        return consultation;
    }
    
    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public LocalDateTime getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(LocalDateTime prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }
    
    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
    
    public static int getPrescriptionIndex() {
        return prescriptionIndex;
    }
    
    public static void setPrescriptionIndex(int index) {
        prescriptionIndex = index;
    }
    
    // Helper methods
    public String getFormattedDate() {
        if (prescriptionDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return prescriptionDate.format(formatter);
        }
        return "Not set";
    }
    
    public String getFormattedValidUntil() {
        if (validUntil != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return validUntil.format(formatter);
        }
        return "Not set";
    }
    
    public void addPrescriptionItem(PrescriptionItem item) {
        if (prescriptionItems == null) {
            prescriptionItems = new ArrayList<>();
        }
        prescriptionItems.add(item);
    }
    
    public void removePrescriptionItem(PrescriptionItem item) {
        if (prescriptionItems != null) {
            prescriptionItems.remove(item);
        }
    }
    
    public boolean isExpired() {
        if (validUntil != null) {
            return LocalDateTime.now().isAfter(validUntil);
        }
        return false;
    }
    
    public double getTotalCost() {
        double total = 0.0;
        if (prescriptionItems != null) {
            for (PrescriptionItem item : prescriptionItems) {
                total += item.getTotalCost();
            }
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "Prescription{" + 
               "prescriptionID='" + prescriptionID + '\'' +
               ", patient=" + (patient != null ? patient.getPatientName() : "N/A") +
               ", doctor=" + (doctor != null ? doctor.getName() : "N/A") +
               ", date=" + getFormattedDate() +
               ", status='" + status + '\'' +
               '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prescription that = (Prescription) obj;
        return prescriptionID.equals(that.prescriptionID);
    }
    
    @Override
    public int hashCode() {
        return prescriptionID.hashCode();
    }
}
