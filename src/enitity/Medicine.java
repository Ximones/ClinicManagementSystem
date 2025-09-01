package enitity;

import java.io.Serializable;

/**
 * Entity class representing medicine information
 *
 * @author Lee Wan Ching
 */
public class Medicine implements Comparable<Medicine>, Serializable {

    private static final long serialVersionUID = 1L;

    private String medicineId;
    private String name;
    private String brandName;
    private String category;
    private String formulation;
    private String dosageForm;
    private int quantity;
    private double price;

    private static int medicineIndex = 0;

    public Medicine(String medicineId, String name, String brandName, String category,
            String formulation, String dosageForm,
            int quantity, double price) {
        this.medicineId = medicineId;
        this.name = name;
        this.brandName = brandName;
        this.category = category;
        this.formulation = formulation;
        this.dosageForm = dosageForm;
        this.quantity = quantity;
        this.price = price;
    }

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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFormulation() {
        return formulation;
    }

    public void setFormulation(String formulation) {
        this.formulation = formulation;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
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
                + ", brandName='" + brandName + '\''
                + ", category='" + category + '\''
                + ", formulation='" + formulation + '\''
                + ", dosageForm='" + dosageForm + '\''
                + ", quantity=" + quantity
                + ", price=" + price
                + '}';
    }

    @Override
    public int compareTo(Medicine other) {
        // First compare by category
        int categoryCompare = this.category.compareToIgnoreCase(other.category);
        if (categoryCompare != 0) {
            return categoryCompare;
        }

        // If category is the same, then compare by name
        int nameCompare = this.name.compareToIgnoreCase(other.name);
        if (nameCompare != 0) {
            return nameCompare;
        }

        // If name also the same, then compare by price
        return Double.compare(this.price, other.price);
    }

}
