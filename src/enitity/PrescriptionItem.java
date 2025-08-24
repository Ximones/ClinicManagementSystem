/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;

/**
 * PrescriptionItem entity class for managing individual items in a prescription
 * @author Zhen Bang
 */
public class PrescriptionItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Medicine medicine;
    private int quantity;
    private String dosage;
    private String frequency;
    private String duration;
    private String specialInstructions;
    private double unitPrice;
    
    public PrescriptionItem() {
    }
    
    public PrescriptionItem(Medicine medicine, int quantity, String dosage, String frequency, String duration) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.unitPrice = medicine != null ? medicine.getPrice() : 0.0;
    }
    
    // Getters and Setters
    public Medicine getMedicine() {
        return medicine;
    }
    
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
        if (medicine != null) {
            this.unitPrice = medicine.getPrice();
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    // Helper methods
    public double getTotalCost() {
        return quantity * unitPrice;
    }
    
    public String getMedicineName() {
        return medicine != null ? medicine.getName() : "Unknown Medicine";
    }
    
    public String getMedicineID() {
        return medicine != null ? medicine.getMedicineId() : "N/A";
    }
    
    @Override
    public String toString() {
        return "PrescriptionItem{" + 
               "medicine=" + getMedicineName() +
               ", quantity=" + quantity +
               ", dosage='" + dosage + '\'' +
               ", frequency='" + frequency + '\'' +
               ", duration='" + duration + '\'' +
               ", totalCost=" + getTotalCost() +
               '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PrescriptionItem that = (PrescriptionItem) obj;
        return medicine.equals(that.medicine) && 
               quantity == that.quantity &&
               dosage.equals(that.dosage) &&
               frequency.equals(that.frequency);
    }
    
    @Override
    public int hashCode() {
        int result = medicine != null ? medicine.hashCode() : 0;
        result = 31 * result + quantity;
        result = 31 * result + (dosage != null ? dosage.hashCode() : 0);
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        return result;
    }
} 