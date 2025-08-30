package control.PharmacyController;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Medicine;
import utility.FileUtils;

/**
 *
 * @author Lee Wan Ching
 */
public class MedicineControl {

    private static final String MEDICINE_DATA_FILE = "medicine";

    public DoublyLinkedList<Pair<String, Medicine>> getAllMedicines() {
        DoublyLinkedList<Pair<String, Medicine>> medicineList
                = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile(MEDICINE_DATA_FILE);

        if (medicineList == null) {
            medicineList = new DoublyLinkedList<>();
        }

        // Update medicine index if list is not empty
        if (!medicineList.isEmpty()) {
            Medicine.setMedicineIndex(medicineList.getSize());
        }

        return medicineList;
    }

    public boolean saveMedicines(DoublyLinkedList<Pair<String, Medicine>> medicineList) {
        try {
            FileUtils.writeDataToFile(MEDICINE_DATA_FILE, medicineList);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving medicines: " + e.getMessage());
            return false;
        }
    }

    public boolean addMedicine(Medicine medicine, DoublyLinkedList<Pair<String, Medicine>> medicineList)
            throws IllegalArgumentException {

        validateMedicine(medicine);

        // Check for duplicates (same name, brand, category, formulation, dosage)
        if (isDuplicateMedicine(medicine, medicineList)) {
            return false;
        }

        String medicineId = generateMedicineId();
        medicine.setMedicineId(medicineId);

        Pair<String, Medicine> newPair = new Pair<>(medicineId, medicine);
        medicineList.insertLast(newPair);

        Medicine.setMedicineIndex(Medicine.getMedicineIndex() + 1);

        return true;
    }

    public boolean updateMedicine(String medicineId, Medicine updatedMedicine,
            DoublyLinkedList<Pair<String, Medicine>> medicineList) throws IllegalArgumentException {

        validateMedicine(updatedMedicine);

        Pair<String, Medicine> existingPair = findMedicineById(medicineId, medicineList);
        if (existingPair == null) {
            return false;
        }

        Medicine existingMedicine = existingPair.getValue();

        // Update all fields except ID
        existingMedicine.setName(updatedMedicine.getName());
        existingMedicine.setBrandName(updatedMedicine.getBrandName());
        existingMedicine.setCategory(updatedMedicine.getCategory());
        existingMedicine.setFormulation(updatedMedicine.getFormulation());
        existingMedicine.setDosageForm(updatedMedicine.getDosageForm());
        existingMedicine.setQuantity(updatedMedicine.getQuantity());
        existingMedicine.setPrice(updatedMedicine.getPrice());

        return true;
    }

    public Pair<String, Medicine> findMedicineById(String medicineId,
            DoublyLinkedList<Pair<String, Medicine>> medicineList) {

        if (medicineId == null || medicineList == null) {
            return null;
        }

        // Sort the list for binary search
        medicineList.sort();

        Pair<String, Medicine> searchKey = new Pair<>(medicineId.toUpperCase(), null);
        return medicineList.binarySearch(searchKey);
    }

    public boolean updateMedicineStock(String medicineId, int quantityChange,
            DoublyLinkedList<Pair<String, Medicine>> medicineList) {

        Pair<String, Medicine> medicinePair = findMedicineById(medicineId, medicineList);
        if (medicinePair == null) {
            return false;
        }

        Medicine medicine = medicinePair.getValue();
        int newQuantity = medicine.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            return false; // Insufficient stock
        }

        medicine.setQuantity(newQuantity);
        return true;
    }

    public int getMedicineStock(String medicineId, DoublyLinkedList<Pair<String, Medicine>> medicineList) {
        Pair<String, Medicine> medicinePair = findMedicineById(medicineId, medicineList);
        if (medicinePair == null) {
            return 0;
        }
        return medicinePair.getValue().getQuantity();
    }

    public DoublyLinkedList<Pair<String, Medicine>> filterMedicines(
            DoublyLinkedList<Pair<String, Medicine>> medicineList,
            String criterion, String searchText) {

        if (searchText == null || searchText.trim().isEmpty()) {
            return medicineList;
        }

        String lowerSearchText = searchText.trim().toLowerCase();
        DoublyLinkedList<Pair<String, Medicine>> filteredList = new DoublyLinkedList<>();

        for (Pair<String, Medicine> pair : medicineList) {
            Medicine med = pair.getValue();
            String id = pair.getKey();
            boolean match = false;

            switch (criterion) {
                case "ID":
                    match = id.toLowerCase().contains(lowerSearchText);
                    break;
                case "Name":
                    match = med.getName().toLowerCase().contains(lowerSearchText);
                    break;
                case "Brand Name":
                    match = med.getBrandName().toLowerCase().contains(lowerSearchText);
                    break;
                case "Category":
                    match = med.getCategory().toLowerCase().contains(lowerSearchText);
                    break;
                case "Formulation":
                    match = med.getFormulation().toLowerCase().contains(lowerSearchText);
                    break;
                case "Dosage":
                    match = med.getDosageForm().toLowerCase().contains(lowerSearchText);
                    break;
                case "Quantity":
                    match = Integer.toString(med.getQuantity()).contains(lowerSearchText);
                    break;
                case "Price":
                    match = String.format("%.2f", med.getPrice()).contains(lowerSearchText);
                    break;
            }

            if (match) {
                filteredList.insertLast(pair);
            }
        }

        return filteredList;
    }

    public void sortMedicines(DoublyLinkedList<Pair<String, Medicine>> medicineList, String sortOrder) {
        if (medicineList == null) {
            return;
        }

        medicineList.sort();

        if ("DESC".equals(sortOrder)) {
            medicineList.reverse();
        }
    }

    public DoublyLinkedList<Pair<String, Medicine>> getLowStockMedicines(
            DoublyLinkedList<Pair<String, Medicine>> medicineList, int threshold) {

        DoublyLinkedList<Pair<String, Medicine>> lowStockList = new DoublyLinkedList<>();

        for (Pair<String, Medicine> pair : medicineList) {
            if (pair.getValue().getQuantity() <= threshold) {
                lowStockList.insertLast(pair);
            }
        }

        return lowStockList;
    }

    private String generateMedicineId() {
        return "MED" + String.format("%03d", Medicine.getMedicineIndex() + 1);
    }

    private boolean isDuplicateMedicine(Medicine newMedicine, DoublyLinkedList<Pair<String, Medicine>> medicineList) {
        for (Pair<String, Medicine> pair : medicineList) {
            Medicine existing = pair.getValue();
            if (existing.getName().equalsIgnoreCase(newMedicine.getName())
                    && existing.getBrandName().equalsIgnoreCase(newMedicine.getBrandName())
                    && existing.getCategory().equalsIgnoreCase(newMedicine.getCategory())
                    && existing.getFormulation().equalsIgnoreCase(newMedicine.getFormulation())
                    && existing.getDosageForm().equalsIgnoreCase(newMedicine.getDosageForm())) {
                return true;
            }
        }
        return false;
    }

    private void validateMedicine(Medicine medicine) throws IllegalArgumentException {
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine cannot be null.");
        }

        if (medicine.getName() == null || medicine.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        }

        if (!medicine.getName().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("Medicine name cannot include numbers or special characters.");
        }

        if (medicine.getBrandName() == null || medicine.getBrandName().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be empty.");
        }

        if (!medicine.getBrandName().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("Brand name cannot include numbers or special characters.");
        }

        if (medicine.getCategory() == null || medicine.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if ("Select Category".equalsIgnoreCase(medicine.getCategory())) {
            throw new IllegalArgumentException("Please select a valid category.");
        }

        if (medicine.getFormulation() == null || medicine.getFormulation().trim().isEmpty()) {
            throw new IllegalArgumentException("Formulation cannot be empty.");
        }
        if ("Select Formulation".equalsIgnoreCase(medicine.getFormulation())) {
            throw new IllegalArgumentException("Please select a valid formulation.");
        }

        if (medicine.getDosageForm() == null || medicine.getDosageForm().trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage cannot be empty.");
        }

        if (!medicine.getDosageForm().matches("[a-zA-Z0-9\\s/]+")) {
            throw new IllegalArgumentException("Dosage contains invalid characters.");
        }

        if (medicine.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        if (medicine.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
    }
}
