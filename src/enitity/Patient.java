/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

/**
 *
 * @author deadb
 */
public class Patient {
    String patientName;
    int patientAge;

    public Patient(String patientName, int patientAge) {
        this.patientName = patientName;
        this.patientAge = patientAge;
    }

    @Override
    public String toString() {
        return "Patient{" + "patientName=" + patientName + ", patientAge=" + patientAge + '}';
    }

}
