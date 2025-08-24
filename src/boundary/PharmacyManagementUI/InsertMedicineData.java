//package boundary.PharmacyManagementUI;
//
//import adt.DoublyLinkedList;
//import adt.Pair;
//import enitity.Medicine;
//import utility.FileUtils;
//
//public class InsertMedicineData {
//
//    public static void main(String[] args) {
//        DoublyLinkedList<Pair<String, Medicine>> medList = new DoublyLinkedList<>();
//
//        medList.insertLast(new Pair<>("MED001", new Medicine("MED001", "Amoxicillin", "Amoxil", "Antibiotic", "Capsule", "250mg", 160, 12.50)));
//        medList.insertLast(new Pair<>("MED002", new Medicine("MED002", "Paracetamol", "Panadol", "Analgesic", "Tablet", "500mg", 120, 5.00)));
//        medList.insertLast(new Pair<>("MED003", new Medicine("MED003", "Ibuprofen", "Nurofen", "Antipyretic", "Tablet", "200mg", 180, 7.80)));
//        medList.insertLast(new Pair<>("MED004", new Medicine("MED004", "Cetirizine", "Zyrtec", "Antihistamine", "Tablet", "10mg", 100, 15.20)));
//        medList.insertLast(new Pair<>("MED005", new Medicine("MED005", "Acyclovir", "Zovirax", "Antiviral", "Capsule", "200mg", 50, 25.00)));
//        medList.insertLast(new Pair<>("MED006", new Medicine("MED006", "Fluconazole", "Diflucan", "Antifungal", "Tablet", "150mg", 40, 30.00)));
//        medList.insertLast(new Pair<>("MED007", new Medicine("MED007", "Fluoxetine", "Prozac", "Antidepressant", "Capsule", "20mg", 210, 45.00)));
//        medList.insertLast(new Pair<>("MED008", new Medicine("MED008", "Atorvastatin", "Lipitor", "Cardiovascular", "Tablet", "10mg", 50, 30.00)));
//        medList.insertLast(new Pair<>("MED009", new Medicine("MED009", "Salbutamol", "Ventolin", "Respiratory", "Inhaler", "100mcg", 20, 25.00)));
//        medList.insertLast(new Pair<>("MED010", new Medicine("MED010", "Omeprazole", "Losec", "Gastrointestinal", "Capsule", "20mg", 150, 18.90)));
//        medList.insertLast(new Pair<>("MED011", new Medicine("MED011", "Hydrocortisone Cream", "Cortizone", "Dermatological", "Cream", "1%", 35, 12.00)));
//        medList.insertLast(new Pair<>("MED012", new Medicine("MED012", "Vitamin C", "Redoxon", "Vitamin / Supplement", "Tablet", "500mg", 100, 8.50)));
//        medList.insertLast(new Pair<>("MED013", new Medicine("MED013", "Prednisolone", "Deltacortril", "Other", "Tablet", "5mg", 30, 15.00)));
//        medList.insertLast(new Pair<>("MED014", new Medicine("MED014", "Dexamethasone Injection", "Decadron", "Other", "Injection", "4mg/ml", 125, 18.00)));
//        medList.insertLast(new Pair<>("MED015", new Medicine("MED015", "Morphine", "MS Contin", "Other", "Tablet", "10mg", 20, 55.00)));
//        medList.insertLast(new Pair<>("MED016", new Medicine("MED016", "Tramadol", "Tramal", "Analgesic", "Capsule", "50mg", 30, 38.00)));
//        medList.insertLast(new Pair<>("MED017", new Medicine("MED017", "Paracetamol Syrup", "Panadol Syrup", "Analgesic", "Syrup", "120mg/5ml", 110, 6.50)));
//        medList.insertLast(new Pair<>("MED018", new Medicine("MED018", "Clotrimazole Cream", "Canesten", "Antifungal", "Cream", "1%", 140, 10.00)));
//        medList.insertLast(new Pair<>("MED019", new Medicine("MED019", "Hydrocortisone Ointment", "Cortaid", "Dermatological", "Ointment", "1%", 35, 11.50)));
//        medList.insertLast(new Pair<>("MED020", new Medicine("MED020", "Zinc Oxide Paste", "Sudocrem", "Dermatological", "Paste", "15%", 225, 9.00)));
//        medList.insertLast(new Pair<>("MED021", new Medicine("MED021", "Vitamin D Drops", "D-Drops", "Vitamin / Supplement", "Drops", "400 IU", 60, 12.00)));
//        medList.insertLast(new Pair<>("MED022", new Medicine("MED022", "Loratadine Syrup", "Clarityne Syrup", "Antihistamine", "Syrup", "5mg/5ml", 50, 13.50)));
//        medList.insertLast(new Pair<>("MED023", new Medicine("MED023", "Amoxicillin Powder", "Amoxil Powder", "Antibiotic", "Powder", "125mg/5ml", 140, 14.00)));
//        medList.insertLast(new Pair<>("MED024", new Medicine("MED024", "Fluconazole Suspension", "Diflucan Suspension", "Antifungal", "Liquid", "50mg/5ml", 30, 20.00)));
//        medList.insertLast(new Pair<>("MED025", new Medicine("MED025", "Diclofenac Gel", "Voltaren Gel", "Analgesic", "Gel", "1%", 45, 17.00)));
//        medList.insertLast(new Pair<>("MED026", new Medicine("MED026", "Nicotine Patch", "Nicorette", "Cardiovascular", "Patch", "21mg", 20, 40.00)));
//        medList.insertLast(new Pair<>("MED027", new Medicine("MED027", "Salbutamol Spray", "Ventolin Spray", "Respiratory", "Spray", "100mcg", 25, 28.00)));
//        medList.insertLast(new Pair<>("MED028", new Medicine("MED028", "Acyclovir Ointment", "Zovirax Ointment", "Antiviral", "Ointment", "5%", 130, 22.00)));
//        medList.insertLast(new Pair<>("MED029", new Medicine("MED029", "Loperamide Capsule", "Imodium", "Gastrointestinal", "Capsule", "2mg", 90, 15.00)));
//        medList.insertLast(new Pair<>("MED030", new Medicine("MED030", "Diazepam Suppository", "Valium Suppository", "Antidepressant", "Suppository", "5mg", 120, 32.00)));
//
//        FileUtils.writeDataToFile("medicine", medList);
//
//        System.out.println("30 medicines have been saved to dao/medicine.bin");
//    }
//}
