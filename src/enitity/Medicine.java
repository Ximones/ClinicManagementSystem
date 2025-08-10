package enitity;

import java.io.Serializable;

public class Medicine implements Serializable {

    private static final long serialVersionUID = 1L;

    private String medicineId;
    private String name;
    private String type;
    private int quantity;
    private double price;
    private static int medicineIndex = 0;

    public Medicine(String medicineId, String name, String type, int quantity, double price) {
        this.medicineId = medicineId;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    // Add these methods for the counter
    public static int getMedicineIndex() {
        return medicineIndex;
    }

    public static void setMedicineIndex(int index) {
        medicineIndex = index;
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

    @Override
    public String toString() {
        return "Medicine{"
                + "medicineId='" + medicineId + '\''
                + ", name='" + name + '\''
                + ", type='" + type + '\''
                + ", quantity=" + quantity
                + ", price=" + price
                + '}';
    }
}
