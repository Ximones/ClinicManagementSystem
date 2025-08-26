/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Doctor;
import enitity.Medicine;
import java.io.File;

/**
 *
 * @author deadb
 */
public class InitializeDataUtils {

    public static void initializeData() {

        // Ensure doctors exist (kept as before)
        DoublyLinkedList<Pair<String, Doctor>> doctorList = new DoublyLinkedList<>();
        DoublyLinkedList<Pair<String, Medicine>> medList = new DoublyLinkedList<>();

        // Initialize Doctor Test Data
        Doctor doc1 = new Doctor("Simon", 20, "01118566866", "Doctor", "Present");
        Doctor doc2 = new Doctor("ZB", 21, "01118566866", "Doctor", "Absent");
        Doctor doc3 = new Doctor("JY", 30, "01118566866", "Consultant", "Resigned");
        Doctor doc4 = new Doctor("Desmond", 32, "01118566866", "Internship", "Present");

        Pair<String, Doctor> doctorPair1 = new Pair<>(doc1.getDoctorID(), doc1);
        Pair<String, Doctor> doctorPair2 = new Pair<>(doc2.getDoctorID(), doc2);
        Pair<String, Doctor> doctorPair3 = new Pair<>(doc3.getDoctorID(), doc3);
        Pair<String, Doctor> doctorPair4 = new Pair<>(doc4.getDoctorID(), doc4);

        doctorList.insertFirst(doctorPair1);
        doctorList.insertLast(doctorPair2);
        doctorList.insertLast(doctorPair3);
        doctorList.insertLast(doctorPair4);

        FileUtils.writeDataToFile("doctors", doctorList);

        // Initialize Medical Test Data (write if file missing OR empty)
        boolean shouldWriteMedicines = false;
        try {
            DoublyLinkedList<Pair<String, Medicine>> existing = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
            if (existing == null || existing.isEmpty()) {
                shouldWriteMedicines = true;
            }
        } catch (Exception e) {
            shouldWriteMedicines = true;
        }

        if (!shouldWriteMedicines) {
            return; // medicine file already populated
        }

        medList.insertLast(new Pair<>("MED001", new Medicine("MED001", "Amoxicillin", "Amoxil", "Antibiotic", "Capsule", "250mg", 160, 12.50)));
        medList.insertLast(new Pair<>("MED002", new Medicine("MED002", "Paracetamol", "Panadol", "Analgesic", "Tablet", "500mg", 300, 0.50)));
        medList.insertLast(new Pair<>("MED003", new Medicine("MED003", "Ibuprofen", "Nurofen", "Anti-inflammatory", "Tablet", "200mg", 200, 0.80)));
        medList.insertLast(new Pair<>("MED004", new Medicine("MED004", "Cetirizine", "Zyrtec", "Antihistamine", "Tablet", "10mg", 150, 0.90)));
        medList.insertLast(new Pair<>("MED005", new Medicine("MED005", "Acyclovir", "Zovirax", "Antiviral", "Capsule", "200mg", 100, 1.20)));
        medList.insertLast(new Pair<>("MED006", new Medicine("MED006", "Fluconazole", "Diflucan", "Antifungal", "Tablet", "150mg", 80, 3.50)));
        medList.insertLast(new Pair<>("MED007", new Medicine("MED007", "Fluoxetine", "Prozac", "Antidepressant", "Capsule", "20mg", 120, 2.00)));
        medList.insertLast(new Pair<>("MED008", new Medicine("MED008", "Atorvastatin", "Lipitor", "Lipid-lowering", "Tablet", "10mg", 140, 1.80)));
        medList.insertLast(new Pair<>("MED009", new Medicine("MED009", "Salbutamol", "Ventolin", "Bronchodilator", "Inhaler", "100mcg", 90, 12.00)));
        medList.insertLast(new Pair<>("MED010", new Medicine("MED010", "Omeprazole", "Losec", "PPI", "Capsule", "20mg", 110, 1.60)));
        medList.insertLast(new Pair<>("MED011", new Medicine("MED011", "Hydrocortisone Cream", "Cortef", "Steroid", "Cream", "1%", 75, 5.00)));
        medList.insertLast(new Pair<>("MED012", new Medicine("MED012", "Vitamin C", "Ascorbic Acid", "Supplement", "Tablet", "1000mg", 200, 0.40)));
        medList.insertLast(new Pair<>("MED013", new Medicine("MED013", "Prednisolone", "Deltasone", "Steroid", "Tablet", "5mg", 150, 0.70)));
        medList.insertLast(new Pair<>("MED014", new Medicine("MED014", "Dexamethasone Injection", "Decadron", "Steroid", "Injection", "4mg/mL", 60, 4.50)));
        medList.insertLast(new Pair<>("MED015", new Medicine("MED015", "Morphine", "MS Contin", "Analgesic", "Tablet", "10mg", 50, 6.00)));
        medList.insertLast(new Pair<>("MED016", new Medicine("MED016", "Tramadol", "Ultram", "Analgesic", "Capsule", "50mg", 100, 1.20)));
        medList.insertLast(new Pair<>("MED017", new Medicine("MED017", "Paracetamol Syrup", "Tylenol", "Analgesic", "Syrup", "120mg/5mL", 130, 0.30)));
        medList.insertLast(new Pair<>("MED018", new Medicine("MED018", "Clotrimazole Cream", "Canesten", "Antifungal", "Cream", "1%", 85, 4.00)));
        medList.insertLast(new Pair<>("MED019", new Medicine("MED019", "Hydrocortisone Ointment", "Cortef Oint", "Steroid", "Ointment", "1%", 45, 5.50)));
        medList.insertLast(new Pair<>("MED020", new Medicine("MED020", "Zinc Oxide Paste", "Sudocrem", "Dermatological", "Paste", "", 70, 3.20)));
        medList.insertLast(new Pair<>("MED021", new Medicine("MED021", "Vitamin D Drops", "Colecalciferol", "Supplement", "Drops", "400IU", 90, 2.20)));
        medList.insertLast(new Pair<>("MED022", new Medicine("MED022", "Loratadine Syrup", "Claritin", "Antihistamine", "Syrup", "5mg/5mL", 95, 2.00)));
        medList.insertLast(new Pair<>("MED023", new Medicine("MED023", "Amoxicillin Powder", "Amoxil Powder", "Antibiotic", "Powder", "250mg/5mL", 110, 6.50)));
        medList.insertLast(new Pair<>("MED024", new Medicine("MED024", "Fluconazole Suspension", "Diflucan Susp", "Antifungal", "Liquid", "50mg/5mL", 60, 7.20)));
        medList.insertLast(new Pair<>("MED025", new Medicine("MED025", "Diclofenac Gel", "Voltaren Gel", "Anti-inflammatory", "Gel", "1%", 45, 3.80)));
        medList.insertLast(new Pair<>("MED026", new Medicine("MED026", "Nicotine Patch", "Nicorette", "Cardiovascular", "Patch", "21mg", 20, 40.00)));
        medList.insertLast(new Pair<>("MED027", new Medicine("MED027", "Salbutamol Spray", "Ventolin Spray", "Respiratory", "Spray", "100mcg", 25, 28.00)));
        medList.insertLast(new Pair<>("MED028", new Medicine("MED028", "Acyclovir Ointment", "Zovirax Ointment", "Antiviral", "Ointment", "5%", 130, 22.00)));
        medList.insertLast(new Pair<>("MED029", new Medicine("MED029", "Loperamide Capsule", "Imodium", "Gastrointestinal", "Capsule", "2mg", 90, 15.00)));
        medList.insertLast(new Pair<>("MED030", new Medicine("MED030", "Diazepam Suppository", "Valium Suppository", "Antidepressant", "Suppository", "5mg", 120, 32.00)));

        FileUtils.writeDataToFile("medicine", medList);

        System.out.println("30 medicines have been saved to dao/medicine.bin");
    }
}
