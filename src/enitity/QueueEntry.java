package enitity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Represents a queue entry for a patient, including timing info.
 *
 * @author szepi
 */
public class QueueEntry implements Comparable<QueueEntry>, Serializable {
    private Patient patient;
    private String queueNumber;
    private String status;
    private long enqueueTime;       // time in ms when patient joined queue
    private long startConsultTime;  // time in ms when consultation starts
    private LocalDateTime startTime; // precise time for consulting
    private long endConsultTime;    // time in ms when consultation ends
    private long consultDuration;   // duration in ms
    private long waitingTime;

    
    public QueueEntry(Patient patient, String queueNumber, String status) {
        this.patient = patient;
        this.queueNumber = queueNumber;
        this.status = status;
        this.enqueueTime = System.currentTimeMillis(); // record entry time
        this.startConsultTime = 0; // not started yet
        this.startTime = null;     // not started yet
    }

    // --- Getters ---
    public Patient getPatient() {
        return patient;
    }
    public String getQueueNumber() {
        return queueNumber;
    }
    public String getStatus() {
        return status;
    }
    public long getEnqueueTime() {
        return enqueueTime;
    }
    public long getStartConsultTime() {
        return startConsultTime;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public long getWaitingTime() {
    return waitingTime;
}
    // --- Setters ---
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setEnqueueTime(long enqueueTime) {
    this.enqueueTime = enqueueTime;
    }

    public void setStartConsultTime(long startConsultTime) {
    this.startConsultTime = startConsultTime;
    }
    public void setWaitingTime(long waitingTime) {
    this.waitingTime = waitingTime;
}


    /**
     * Marks the start of consultation, sets status to "In Progress".
     */
    public void markStartConsult() {
        this.status = "In Progress";
        this.startConsultTime = System.currentTimeMillis();
        this.startTime = LocalDateTime.now();
    }

    /**
     * Marks the consultation as done, sets status to "Done".
     */
    public void markConsultationDone() {
        this.status = "Done";
    }

    /**
     * Marks the patient as consulting, sets status to "Consulting"
     * and records LocalDateTime.
     */
    public void markConsulting() {
        this.status = "Consulting";
        this.startConsultTime = System.currentTimeMillis();
        this.startTime = LocalDateTime.now();  // <-- required
    }

    /**
     * Marks the patient as prescribing after consultation.
     */
    public void markPrescriptioning() {
        this.status = "Prescribing";
    }

    // --- Waiting Time ---
    /**
     * Returns waiting time in ms (enqueue → start consult OR enqueue → now).
     */
    public long getWaitingTimeMillis() {
        if (startConsultTime > 0) {
            return startConsultTime - enqueueTime; // already started
        }
        return System.currentTimeMillis() - enqueueTime; // still waiting
    }

    // --- Utility methods for display ---
    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "-"; // not set yet
        }
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timeMillis));
    }

    private String formatDateTime(LocalDateTime time) {
        if (time == null) return "-";
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedEnqueueTime() {
        return formatTime(enqueueTime);
    }

    public String getFormattedStartTime() {
        return formatTime(startConsultTime);
    }

    public String getFormattedStartDateTime() {
        return formatDateTime(startTime);
    }

    /**
     * Returns formatted waiting time (e.g. "2m 34s" or "45s").
     */
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
    
    
    public void setEndConsultTime(long endConsultTime) {
    this.endConsultTime = endConsultTime;
    }

    public long getEndConsultTime() {
    return endConsultTime;
    }

    public void setConsultDuration(long consultDuration) {
    this.consultDuration = consultDuration;
    }

    public long getConsultDuration() {
    return consultDuration;
    }

    @Override
    public String toString() {
        return "QueueEntry{"
                + "queueNumber='" + queueNumber + '\''
                + ", patient=" + (patient != null ? patient.getPatientID() : "null")
                + ", status='" + status + '\''
                + ", enqueue=" + getFormattedEnqueueTime()
                + ", start(ms)=" + getFormattedStartTime()
                + ", startDateTime=" + getFormattedStartDateTime()
                + ", waiting=" + getFormattedWaitingTime()
                + '}';
    }
    
    
    
    
   @Override
    public int compareTo(QueueEntry other) {
//        // First, compare by day of the week
//        int dayCompare = this.dayOfWeek.compareTo(other.dayOfWeek);
//        if (dayCompare != 0) {
//            return dayCompare;
//        }
//        // If days are the same, then compare by shift
//        return this.shift.compareTo(other.shift);
       return this.queueNumber.compareTo(other.queueNumber);
    }

}