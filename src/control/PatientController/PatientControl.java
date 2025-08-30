/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.PatientController;


import adt.DoublyLinkedList;
import boundary.PatientManagementUI.PatientDialog;
import enitity.Patient;
import javax.swing.JOptionPane;
import utility.FileUtils;
/**
 *
 * @author szepi
 */
public class PatientControl {
    private DoublyLinkedList<Patient> patientList;

    public PatientControl() {
        loadPatients();
    }

    private void loadPatients() {
        patientList = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
        if (patientList == null) {
            patientList = new DoublyLinkedList<>();
        }
    }

    public void savePatients() {
        FileUtils.writeDataToFile("patients", patientList);
    }

    public DoublyLinkedList<Patient> getAllPatients() {
        return patientList;
    }

    public Patient searchPatientById(String id) {
        for (Patient p : patientList) {
            if (p.getPatientID().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public void addPatient(Patient p) {
        patientList.insertLast(p);
        savePatients();
    }

    public void updatePatient(Patient updated) {
        Patient existing = searchPatientById(updated.getPatientID());
        if (existing != null) {
            existing.setPatientName(updated.getPatientName());
            existing.setPatientAge(updated.getPatientAge());
            existing.setPatientIC(updated.getPatientIC());
            existing.setGender(updated.getGender());
            existing.setContact(updated.getContact());
            existing.setEmail(updated.getEmail());
            existing.setAddress(updated.getAddress());
            existing.setDateOfRegistration(updated.getDateOfRegistration());
        }
    }

    public void sortById(boolean ascending) {
        patientList.sort();
        if (!ascending) {
            patientList.reverse();
        }
    }
   
    
   public String generateNextPatientId() {
    if (patientList.isEmpty()) {
        return "p001";
    }
    String lastId = patientList.getLast().getEntry().getPatientID(); // get tail's patient
    int num = Integer.parseInt(lastId.substring(1)); // strip 'p'
    return String.format("P%03d", num + 1);
}


    
}