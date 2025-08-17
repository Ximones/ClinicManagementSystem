/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a queue entry for a patient, including timing info.
 * 
 * @author szepi
 */
public class QueueEntry implements Serializable {
    private Patient patient;
    private String queueNumber;
    private String status;
    private long enqueueTime;       // time in ms when patient joined queue
    private long startConsultTime;  // time in ms when consultation starts

    public QueueEntry(Patient patient, String queueNumber, String status) {
        this.patient = patient;
        this.queueNumber = queueNumber;
        this.status = status;
        this.enqueueTime = System.currentTimeMillis(); // record entry time
        this.startConsultTime = 0; // not started yet
    }

    // --- Getters ---
    public Patient getPatient() { return patient; }
    public String getQueueNumber() { return queueNumber; }
    public String getStatus() { return status; }
    public long getEnqueueTime() { return enqueueTime; }
    public long getStartConsultTime() { return startConsultTime; }

    // --- Setters ---
    public void setStatus(String status) { this.status = status; }

    /** Marks the start of consultation, sets status to "In Progress". */
    public void markStartConsult() {
        this.status = "In Progress";
        this.startConsultTime = System.currentTimeMillis();
    }

    // --- Waiting Time ---
    /** Returns waiting time in ms (enqueue → start consult OR enqueue → now). */
    public long getWaitingTimeMillis() {
        if (startConsultTime > 0) {
            return startConsultTime - enqueueTime; // already started
        }
        return System.currentTimeMillis() - enqueueTime; // still waiting
    }

    // --- Utility methods for display ---
    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) return "-"; // not set yet
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timeMillis));
    }

    public String getFormattedEnqueueTime() { return formatTime(enqueueTime); }
    public String getFormattedStartTime() { return formatTime(startConsultTime); }

    /** Returns formatted waiting time (e.g. "2m 34s" or "45s"). */
    public String getFormattedWaitingTime() {
        long ms = getWaitingTimeMillis();
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    @Override
    public String toString() {
        return "QueueEntry{" +
                "queueNumber='" + queueNumber + '\'' +
                ", patient=" + (patient != null ? patient.getPatientID() : "null") +
                ", status='" + status + '\'' +
                ", enqueue=" + getFormattedEnqueueTime() +
                ", start=" + getFormattedStartTime() +
                ", waiting=" + getFormattedWaitingTime() +
                '}';
    }
}
