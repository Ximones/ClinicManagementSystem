package enitity;

import java.io.Serializable;
import java.util.Objects;

public class DispenseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String patientName;
    private String doctorName;
    private String medicineName;
    private int quantity;
    private String dateTime;

    public DispenseRecord(String patientName, String doctorName, String medicineName, int quantity, String dateTime) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dateTime = dateTime;
    }

    public DispenseRecord() {
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "DispenseRecord{" +
                "patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispenseRecord)) return false;
        DispenseRecord that = (DispenseRecord) o;
        return quantity == that.quantity &&
                Objects.equals(patientName, that.patientName) &&
                Objects.equals(doctorName, that.doctorName) &&
                Objects.equals(medicineName, that.medicineName) &&
                Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientName, doctorName, medicineName, quantity, dateTime);
    }
}