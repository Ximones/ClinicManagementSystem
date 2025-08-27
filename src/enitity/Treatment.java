/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 * @author deadb
 */
public class Treatment implements Serializable, Comparable<Treatment> {

    private static final long serialVersionUID = 1L;
    private static int treatmentIndex = 0;

    private String treatmentID;
    private Consultation consultation; // Link to the consultation where the treatment was decided
    private String diagnosis;        // The diagnosis that led to this treatment
    private String treatmentDetails; // Description of the treatment (e.g., "Wound stitching", "Vaccination")
    private LocalDateTime treatmentDateTime;
    private double cost;
    private String notes;            // Any additional notes from the doctor

    public Treatment() {
        this.treatmentID = generateTreatmentID();
        this.treatmentDateTime = LocalDateTime.now();
    }

    public Treatment(Consultation consultation, String diagnosis, String treatmentDetails, double cost, String notes) {
        this.treatmentID = generateTreatmentID();
        this.consultation = consultation;
        this.diagnosis = diagnosis;
        this.treatmentDetails = treatmentDetails;
        this.cost = cost;
        this.notes = notes;
        this.treatmentDateTime = LocalDateTime.now();
    }

    private static String generateTreatmentID() {
        treatmentIndex++;
        return String.format("T%03d", treatmentIndex); // e.g., T001, T002
    }

    // --- Getters and Setters ---
    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    public LocalDateTime getTreatmentDateTime() {
        return treatmentDateTime;
    }

    public void setTreatmentDateTime(LocalDateTime treatmentDateTime) {
        this.treatmentDateTime = treatmentDateTime;
    }
    
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static void setTreatmentIndex(int index) {
        treatmentIndex = index;
    }
    
    public String getFormattedDateTime() {
        if (treatmentDateTime != null) {
            return treatmentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "treatmentID='" + treatmentID + '\'' +
                ", patient=" + (consultation != null ? consultation.getPatient().getPatientName() : "N/A") +
                ", diagnosis='" + diagnosis + '\'' +
                ", details='" + treatmentDetails + '\'' +
                ", dateTime=" + getFormattedDateTime() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treatment treatment = (Treatment) o;
        return Objects.equals(treatmentID, treatment.treatmentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentID);
    }

    @Override
    public int compareTo(Treatment other) {
        // Sorts treatments by date, from newest to oldest
        return other.getTreatmentDateTime().compareTo(this.getTreatmentDateTime());
    }
}
