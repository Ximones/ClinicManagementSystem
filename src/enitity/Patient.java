/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author szepi
 */
public class Patient implements Comparable<Patient>, Serializable {

    private static final long serialVersionUID = 1L;
    private static int patientIndex = 0;
    private LocalDateTime enqueueTime;
    private LocalDateTime startTime;
    String patientID;
    String patientName;
    int patientAge;
    String patientIC;
    String gender;
    String contact;
    String email;
    String address;
    String dateOfRegistration;
    String status;

    public Patient() {
        this.patientID = generatePatientID();
        // Default status or other initializations if needed
        this.status = "Active"; // Example
    }

    public Patient(String id, String patientName, String patientIC, int patientAge, String gender, String contact, String email, String address, String dateOfRegistration) {
        this.patientID = id;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientIC = patientIC;
        this.gender = gender;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.dateOfRegistration = dateOfRegistration;
        this.status = "Active";
    }

    public LocalDateTime getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(LocalDateTime enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    private static String generatePatientID() {
        patientIndex++;
        return String.format("P%03d", patientIndex); // e.g., P001, P002
    }

    // Static getters/setters for patientIndex
    public static int getPatientIndex() {
        return patientIndex;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public static void setPatientIndex(int index) {
        patientIndex = index;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientIC() {
        return patientIC;
    }

    public void setPatientIC(String patientIC) {
        this.patientIC = patientIC;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
    if (gender == null) {
        this.gender = null;
    } else {
        String g = gender.trim().toLowerCase();
        if (g.startsWith("m")) this.gender = "Male";
        else if (g.startsWith("f")) this.gender = "Female";
        else this.gender = gender.trim(); 
    }
}

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(String dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getStatus() { // Getter for status
        return status;
    }

    public void setStatus(String status) { // Setter for status
        this.status = status;
    }

    @Override
    public String toString() {
        return "Patient{"
                + "Patient ID='" + patientID + '\''
                + // Added Patient ID to toString
                ", Name='" + patientName + '\''
                + ", Age=" + patientAge
                + ", IC='" + patientIC + '\''
                + ", Gender='" + gender + '\''
                + ", Contact='" + contact + '\''
                + ", Email='" + email + '\''
                + ", Address='" + address + '\''
                + ", Date of Registration='" + dateOfRegistration + '\''
                + ", Status='" + status + '\''
                + // Added Status to toString
                '}';
    }

    @Override
    public int compareTo(Patient other) {
//        // First, compare by day of the week
//        int dayCompare = this.dayOfWeek.compareTo(other.dayOfWeek);
//        if (dayCompare != 0) {
//            return dayCompare;
//        }
//        // If days are the same, then compare by shift
//        return this.shift.compareTo(other.shift);
         return this.patientID.compareTo(other.patientID);
    }
}
