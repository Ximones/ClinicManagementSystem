/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Consultation entity class for managing patient consultations
 * @author Zhen Bang
 */
public class Consultation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static int consultationIndex = 0;
    
    private String consultationID;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime consultationDateTime;
    private String consultationType; // "New Visit", "Follow-up", "Emergency"
    private String status; // "Scheduled", "In Progress", "Completed", "Cancelled"
    private String symptoms;
    private String diagnosis;
    private String notes;
    private LocalDateTime followUpDateTime;
    private boolean requiresFollowUp;
    
    public Consultation() {
        this.consultationID = generateConsultationID();
        this.status = "Scheduled";
        this.requiresFollowUp = false;
    }
    
    public Consultation(Patient patient, Doctor doctor, LocalDateTime consultationDateTime, 
                       String consultationType, String symptoms) {
        this.consultationID = generateConsultationID();
        this.patient = patient;
        this.doctor = doctor;
        this.consultationDateTime = consultationDateTime;
        this.consultationType = consultationType;
        this.symptoms = symptoms;
        this.status = "Scheduled";
        this.requiresFollowUp = false;
    }
    
    private static String generateConsultationID() {
        consultationIndex++;
        return String.format("C%03d", consultationIndex);
    }
    
    // Getters and Setters
    public String getConsultationID() {
        return consultationID;
    }
    
    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
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
    
    public LocalDateTime getConsultationDateTime() {
        return consultationDateTime;
    }
    
    public void setConsultationDateTime(LocalDateTime consultationDateTime) {
        this.consultationDateTime = consultationDateTime;
    }
    
    public String getConsultationType() {
        return consultationType;
    }
    
    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getFollowUpDateTime() {
        return followUpDateTime;
    }
    
    public void setFollowUpDateTime(LocalDateTime followUpDateTime) {
        this.followUpDateTime = followUpDateTime;
    }
    
    public boolean isRequiresFollowUp() {
        return requiresFollowUp;
    }
    
    public void setRequiresFollowUp(boolean requiresFollowUp) {
        this.requiresFollowUp = requiresFollowUp;
    }
    
    public static int getConsultationIndex() {
        return consultationIndex;
    }
    
    public static void setConsultationIndex(int index) {
        consultationIndex = index;
    }
    
    // Helper methods
    public String getFormattedDateTime() {
        if (consultationDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return consultationDateTime.format(formatter);
        }
        return "Not set";
    }
    
    public String getFormattedFollowUpDateTime() {
        if (followUpDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return followUpDateTime.format(formatter);
        }
        return "Not scheduled";
    }
    
    @Override
    public String toString() {
        return "Consultation{" + 
               "consultationID='" + consultationID + '\'' +
               ", patient=" + (patient != null ? patient.getPatientName() : "N/A") +
               ", doctor=" + (doctor != null ? doctor.getName() : "N/A") +
               ", dateTime=" + getFormattedDateTime() +
               ", type='" + consultationType + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Consultation that = (Consultation) obj;
        return consultationID.equals(that.consultationID);
    }
    
    @Override
    public int hashCode() {
        return consultationID.hashCode();
    }
}
