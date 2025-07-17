package enitity;

import java.time.LocalDate;

/**
 *
 * @author Lee Wan Ching
 */

public class Medicine {

    private String medicineId;
    private String name;
    private String type;
    private int quantity;
    private double price;
    private LocalDate expiryDate;

    public Medicine(String medicineId, String name, String type, int quantity, double price, LocalDate expiryDate) {
        this.medicineId = medicineId;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }
    
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Medicine ID: " + medicineId +
               "\nName: " + name +
               "\nType: " + type +
               "\nQuantity: " + quantity +
               "\nPrice: RM " + price +
               "\nExpiry Date: " + expiryDate;
    }
}