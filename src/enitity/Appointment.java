/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appointment entity class for managing follow-up appointments
 * @author Zhen Bang
 */
public class Appointment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static int appointmentIndex = 0;
    
    private String appointmentID;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDateTime;
    private String appointmentType; // "Follow-up", "Review", "Check-up"
    private String status; // "Scheduled", "Confirmed", "Completed", "Cancelled", "No-show"
    private String reason;
    private String notes;
    private Consultation originalConsultation; // Reference to the original consultation
    
    public Appointment() {
        this.appointmentID = generateAppointmentID();
        this.status = "Scheduled";
    }
    
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDateTime, 
                      String appointmentType, String reason, Consultation originalConsultation) {
        this.appointmentID = generateAppointmentID();
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.appointmentType = appointmentType;
        this.reason = reason;
        this.originalConsultation = originalConsultation;
        this.status = "Scheduled";
    }
    
    private static String generateAppointmentID() {
        appointmentIndex++;
        return String.format("A%03d", appointmentIndex);
    }
    
    // Getters and Setters
    public String getAppointmentID() {
        return appointmentID;
    }
    
    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
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
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public String getAppointmentType() {
        return appointmentType;
    }
    
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Consultation getOriginalConsultation() {
        return originalConsultation;
    }
    
    public void setOriginalConsultation(Consultation originalConsultation) {
        this.originalConsultation = originalConsultation;
    }
    
    public static int getAppointmentIndex() {
        return appointmentIndex;
    }
    
    public static void setAppointmentIndex(int index) {
        appointmentIndex = index;
    }
    
    // Helper methods
    public String getFormattedDateTime() {
        if (appointmentDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return appointmentDateTime.format(formatter);
        }
        return "Not set";
    }
    
    public boolean isUpcoming() {
        if (appointmentDateTime != null) {
            return appointmentDateTime.isAfter(LocalDateTime.now()) && 
                   !status.equals("Completed") && !status.equals("Cancelled");
        }
        return false;
    }
    
    public boolean isToday() {
        if (appointmentDateTime != null) {
            LocalDateTime now = LocalDateTime.now();
            return appointmentDateTime.toLocalDate().equals(now.toLocalDate());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Appointment{" + 
               "appointmentID='" + appointmentID + '\'' +
               ", patient=" + (patient != null ? patient.getPatientName() : "N/A") +
               ", doctor=" + (doctor != null ? doctor.getName() : "N/A") +
               ", dateTime=" + getFormattedDateTime() +
               ", type='" + appointmentType + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return appointmentID.equals(that.appointmentID);
    }
    
    @Override
    public int hashCode() {
        return appointmentID.hashCode();
    }
} 