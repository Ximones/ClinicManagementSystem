/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;

/**
 *
 * @author szepi
 */
public class QueueEntry implements Serializable {
    private Patient patient;
    private String queueNumber;
    private String status;

    public QueueEntry(Patient patient, String queueNumber, String status) {
        this.patient = patient;
        this.queueNumber = queueNumber;
        this.status = status;
    }

    public Patient getPatient() { return patient; }
    public String getQueueNumber() { return queueNumber; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
