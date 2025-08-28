/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Doctor;
import enitity.Medicine;
import enitity.Patient;
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
        DoublyLinkedList<Patient> patientList = new DoublyLinkedList<>();
         
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
        
        // Initialize Patient Test Data
        patientList.insertLast(new Patient("P001", "Ali bin Hassan", "900101-14-1122", 35, "Male",
        "012-1111222", "ali.hassan@gmail.com",
        "No. 5, Jalan Damai, Taman Desa, Kuala Lumpur", "2025-08-01"));
        patientList.insertLast(new Patient("P002", "Chong Wei Ling", "980202-10-3344", 27, "Female",
        "013-2222333", "weiling.chong@yahoo.com",
        "Condo Sri Hartamas, Blok C-8-15, Mont Kiara, KL", "2025-08-02"));
        patientList.insertLast(new Patient("P003", "Mohd Faiz bin Rahman", "950315-08-5566", 30, "Male",
        "014-3333444", "faiz.rahman95@gmail.com",
        "No. 88, Jalan Anggerik, Seksyen 7, Shah Alam, Selangor", "2025-08-03"));
        patientList.insertLast(new Patient("P004", "Anita a/p Subramaniam", "930420-14-7788", 32, "Female",
        "015-4444555", "anita.suba@gmail.com",
        "No. 25, Jalan Indah 3, Taman Sri Tebrau, Johor Bahru, Johor", "2025-08-05"));
        patientList.insertLast(new Patient("P005", "Lim Boon Huat", "000519-08-9900", 25, "Male",
        "016-5555666", "boonhuat.lim@gmail.com",
        "No. 7, Jalan Rasah Jaya, Seremban, Negeri Sembilan", "2025-08-07"));
        patientList.insertLast(new Patient("P006", "Nur Amirah binti Ismail", "020103-10-2244", 23, "Female",
        "017-6666777", "amirah.ismail02@gmail.com",
        "No. 33, Jalan Setia Tropika 5/2, Taman Setia Tropika, Johor Bahru", "2025-08-10"));
        patientList.insertLast(new Patient("P007", "Raj a/l Manogaran", "970312-08-3355", 28, "Male",
        "018-7777888", "raj.manogaran@yahoo.com",
        "No. 19, Jalan Transfer, George Town, Penang", "2025-08-12"));
        patientList.insertLast(new Patient("P008", "Tan Siew Mei", "890603-14-4466", 36, "Female",
        "019-8888999", "siewmei.tan@gmail.com",
        "No. 56, Jalan Kota Laksamana, Melaka City, Melaka", "2025-08-15"));
        patientList.insertLast(new Patient("P009", "Ahmad Zaki bin Osman", "990927-10-5577", 26, "Male",
        "011-9999000", "zaki.osman99@yahoo.com",
        "No. 21, Jalan Raja Dr Nazrin, Taman Jubilee, Ipoh, Perak", "2025-08-20"));
        patientList.insertLast(new Patient("P010", "Chia Li Xuan", "960512-14-8899", 29, "Female",
        "012-1212121", "lixuan.chia@yahoo.com",
        "No. 8, Lorong Taman Alor Akar 3, Kuantan, Pahang", "2025-08-25"));

        
        // Initialize Patient Test Data For Report
      
        patientList.insertLast(new Patient("P011", "Gan Mei Yee", "850412-10-1234", 40, "Female",
        "013-8888111", "meiyee.gan@gmail.com",
        "No. 12, Jalan Damansara Heights, Kuala Lumpur", "2025-08-28"));

        patientList.insertLast(new Patient("P012", "Syed Amir bin Jamal", "910727-14-5678", 2, "Male",   // toddler
        "012-3456789", "amir.jamal91@yahoo.com",
        "No. 44, Jalan Dato Sheikh Ahmad, Seremban, Negeri Sembilan", "2025-08-28"));

        patientList.insertLast(new Patient("P013", "Vasanthi a/p Ramesh", "780315-08-2244", 85, "Female", // elderly
        "016-7777333", "vasanthi.ramesh78@gmail.com",
        "No. 99, Jalan Raja Chulan, Kuala Lumpur", "2025-08-28"));

        patientList.insertLast(new Patient("P014", "Jonathan Lee Wei Sheng", "050909-10-8899", 18, "Male", // teenager
        "017-6666222", "jonathanlee05@gmail.com",
        "No. 23, Jalan Song Ban Kheng, Bukit Mertajam, Penang", "2025-08-28"));

        patientList.insertLast(new Patient("P015", "Nurul Hidayah binti Ahmad", "880214-06-4455", 55, "Female",
        "018-9999222", "nurul.hidayah88@gmail.com",
        "No. 17, Jalan Sutera, Taman Sentosa, Johor Bahru", "2025-08-28"));

        patientList.insertLast(new Patient("P016", "Marcus Tan Wei Jian", "940501-08-3344", 70, "Male",
        "019-3333222", "marcus.tan94@yahoo.com",
        "No. 66, Jalan Padungan, Kuching, Sarawak", "2025-08-28"));

        patientList.insertLast(new Patient("P017", "Aisyah binti Salleh", "030725-10-1122", 8, "Female", // child
        "011-2222334", "aisyah.salleh03@gmail.com",
        "No. 5, Jalan Pantai Dalam, Kuala Lumpur", "2025-08-28"));

        patientList.insertLast(new Patient("P018", "Daniel Wong Jun Hao", "990831-14-7788", 33, "Male",
        "014-5555666", "daniel.wong99@gmail.com",
        "No. 45, Jalan Kebun Teh, Johor Bahru, Johor", "2025-08-28"));
 
        patientList.insertLast(new Patient("P019", "Thavamani a/l Krishnan", "760129-10-4466", 90, "Male", // very elderly
        "015-1212121", "thavamani.krishnan76@gmail.com",
        "No. 8, Jalan Laksamana, Melaka City, Melaka", "2025-08-28"));

        patientList.insertLast(new Patient("P020", "Cheong Li Fang", "970618-08-5577", 25, "Female",
        "012-9898989", "lifang.cheong97@yahoo.com",
        "No. 10, Jalan Pasir Puteh, Ipoh, Perak", "2025-08-28"));
        
        FileUtils.writeDataToFile("patients", patientList);
        System.out.println("10 patients have been saved to dao/patients.bin");


  
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
