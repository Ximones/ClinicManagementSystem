package enitity;

import java.io.Serializable;
import java.util.Objects;

public class DispenseRecord implements Serializable, Comparable<DispenseRecord> {

    private static final long serialVersionUID = 1L;

    private String prescriptionId;
    private String patientName;
    private String doctorName;
    private String medicineId;
    private String medicineName;
    private int quantity;
    private double unitPrice;
    private double lineTotal;
    private String dateTime;

    public DispenseRecord(String prescriptionId, String patientName, String doctorName,
            String medicineId, String medicineName,
            int quantity, double unitPrice, double lineTotal,
            String dateTime) {
        this.prescriptionId = prescriptionId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
        this.dateTime = dateTime;
    }

    public DispenseRecord() {
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "DispenseRecord{"
                + "prescriptionId='" + prescriptionId + '\''
                + ", patientName='" + patientName + '\''
                + ", doctorName='" + doctorName + '\''
                + ", medicineId='" + medicineId + '\''
                + ", medicineName='" + medicineName + '\''
                + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice
                + ", lineTotal=" + lineTotal
                + ", dateTime='" + dateTime + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DispenseRecord)) {
            return false;
        }
        DispenseRecord that = (DispenseRecord) o;
        return quantity == that.quantity
                && Double.compare(that.unitPrice, unitPrice) == 0
                && Double.compare(that.lineTotal, lineTotal) == 0
                && Objects.equals(prescriptionId, that.prescriptionId)
                && Objects.equals(patientName, that.patientName)
                && Objects.equals(doctorName, that.doctorName)
                && Objects.equals(medicineId, that.medicineId)
                && Objects.equals(medicineName, that.medicineName)
                && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prescriptionId, patientName, doctorName, medicineId,
                medicineName, quantity, unitPrice, lineTotal, dateTime);
    }

    @Override
    public int compareTo(DispenseRecord other) {
        if (this.dateTime == null && other.dateTime == null) {
            return 0;
        }
        if (this.dateTime == null) {
            return 1;
        }
        if (other.dateTime == null) {
            return -1;
        }

        java.time.LocalDateTime thisDate = java.time.LocalDateTime.parse(this.dateTime);
        java.time.LocalDateTime otherDate = java.time.LocalDateTime.parse(other.dateTime);

        return otherDate.compareTo(thisDate); // DESC
    }

}
