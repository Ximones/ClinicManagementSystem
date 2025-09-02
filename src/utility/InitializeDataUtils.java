package utility;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Doctor;
import enitity.DutySlot;
import enitity.Medicine;
import enitity.Patient;
import enitity.QueueEntry;

/**
 *
 * @author Chok Chun Fai, Lim Sze Ping, Lee Wan Ching
 */
public class InitializeDataUtils {

    public static void initializeData() {

        // Pre-condition to perform reinitialize data
        boolean initializeAllData = false;

        // Check if data exists in corresponding files
        try {

            DoublyLinkedList<Pair<String, Doctor>> existingDoctorData = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
            DoublyLinkedList<DutySlot> existingScheduleData = (DoublyLinkedList<DutySlot>) FileUtils.readDataFromFile("this_week_schedule");
            DoublyLinkedList<Patient> existingPatientData = (DoublyLinkedList<Patient>) FileUtils.readDataFromFile("patients");
            DoublyLinkedList<Pair<String, Medicine>> existingMedicineData = (DoublyLinkedList<Pair<String, Medicine>>) FileUtils.readDataFromFile("medicine");
            DoublyLinkedList<Pair<String, QueueEntry>> existingQueueData = (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
            
            // If condition met, perform reinitialize each field data 
            if (existingDoctorData == null || existingDoctorData.isEmpty()
                    || existingScheduleData == null || existingScheduleData.isEmpty()
                    || existingPatientData == null || existingPatientData.isEmpty()
                    || existingMedicineData == null || existingMedicineData.isEmpty()
                    || existingQueueData == null || existingQueueData.isEmpty()) {
                initializeAllData = true;
            }
        } catch (Exception e) {
            initializeAllData = true;
        }

        // If condition not met, back to normal
        if (!initializeAllData) {
            System.out.println("No data is corrupted and from file reading");
            return;
        }

        //Initialize the data to List
        DoublyLinkedList<Pair<String, Doctor>> doctorList = initializeDoctorData();
        DoublyLinkedList<DutySlot> thisWeekSchedule = initializeScheduleData(doctorList);
        DoublyLinkedList<Patient> patientList = initializePatientData();
        DoublyLinkedList<Pair<String, Medicine>> medList = initializeMedicineData();
        DoublyLinkedList<Pair<String, QueueEntry>> queueList = initializeQueueData(patientList);
        
        
        // Write the List's data to each file
        FileUtils.writeDataToFile("doctors", doctorList);
        FileUtils.writeDataToFile("this_week_schedule", thisWeekSchedule);
        FileUtils.writeDataToFile("patients", patientList);
        FileUtils.writeDataToFile("medicine", medList);
        FileUtils.writeDataToFile("queue", queueList);
        
        System.out.println("Doctors Data have been intialized and saved to dao/doctos.bin");
        System.out.println("This Week Schedule Data have been intialized and saved to dao/this_week_schedule.bin");
        System.out.println("Patients data have been intialized and saved to dao/patients.bin");
        System.out.println("Medicines data have been intialized have been saved to dao/medicine.bin");
        System.out.println("Queue data have been initialized and saved to dao/queue.bin");
    }

    // Initialize Doctor Test Data
    private static DoublyLinkedList<Pair<String, Doctor>> initializeDoctorData() {

        DoublyLinkedList<Pair<String, Doctor>> doctorList = new DoublyLinkedList<>();

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

        return doctorList;
    }

    // Initialize Schedule Test Data
    private static DoublyLinkedList<DutySlot> initializeScheduleData(DoublyLinkedList<Pair<String, Doctor>> doctorList) {

        DoublyLinkedList<DutySlot> thisWeekSchedule = new DoublyLinkedList<>();

        DutySlot dataSlot1 = new DutySlot("Monday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot2 = new DutySlot("Tuesday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot3 = new DutySlot("Wednesday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot4 = new DutySlot("Thursday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot5 = new DutySlot("Friday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot6 = new DutySlot("Saturday", "Morning (8am - 2pm)", doctorList.getElement(1).getEntry().getValue());
        DutySlot dataSlot7 = new DutySlot("Monday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot8 = new DutySlot("Tuesday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot9 = new DutySlot("Wednesday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot10 = new DutySlot("Thursday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot11 = new DutySlot("Friday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());
        DutySlot dataSlot12 = new DutySlot("Saturday", "Evening (2pm - 8pm)", doctorList.getElement(4).getEntry().getValue());

        thisWeekSchedule.insertLast(dataSlot1);
        thisWeekSchedule.insertLast(dataSlot2);
        thisWeekSchedule.insertLast(dataSlot3);
        thisWeekSchedule.insertLast(dataSlot4);
        thisWeekSchedule.insertLast(dataSlot5);
        thisWeekSchedule.insertLast(dataSlot6);
        thisWeekSchedule.insertLast(dataSlot7);
        thisWeekSchedule.insertLast(dataSlot8);
        thisWeekSchedule.insertLast(dataSlot9);
        thisWeekSchedule.insertLast(dataSlot10);
        thisWeekSchedule.insertLast(dataSlot11);
        thisWeekSchedule.insertLast(dataSlot12);

        return thisWeekSchedule;
    }

    // Initialize Patient Test Data
    private static DoublyLinkedList<Patient> initializePatientData() {

        DoublyLinkedList<Patient> patientList = new DoublyLinkedList<>();

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

        
        // Initialize Patient Test Data for report
        patientList.insertLast(new Patient("P011", "Gan Mei Yee", "850412-10-1234", 40, "Female",
                "013-8888111", "meiyee.gan@gmail.com",
                "No. 12, Jalan Damansara Heights, Kuala Lumpur", "2025-08-29"));

        patientList.insertLast(new Patient("P012", "Syed Amir bin Jamal", "910727-14-5678", 2, "Male", // toddler
                "012-3456789", "amir.jamal91@yahoo.com",
                "No. 44, Jalan Dato Sheikh Ahmad, Seremban, Negeri Sembilan", "2025-08-29"));

        patientList.insertLast(new Patient("P013", "Vasanthi a/p Ramesh", "780315-08-2244", 85, "Female", // elderly
                "016-7777333", "vasanthi.ramesh78@gmail.com",
                "No. 99, Jalan Raja Chulan, Kuala Lumpur", "2025-08-29"));

        patientList.insertLast(new Patient("P014", "Jonathan Lee Wei Sheng", "050909-10-8899", 18, "Male", // teenager
                "017-6666222", "jonathanlee05@gmail.com",
                "No. 23, Jalan Song Ban Kheng, Bukit Mertajam, Penang", "2025-08-29"));

        patientList.insertLast(new Patient("P015", "Nurul Hidayah binti Ahmad", "880214-06-4455", 55, "Female",
                "018-9999222", "nurul.hidayah88@gmail.com",
                "No. 17, Jalan Sutera, Taman Sentosa, Johor Bahru", "2025-08-29"));

        patientList.insertLast(new Patient("P016", "Marcus Tan Wei Jian", "940501-08-3344", 70, "Male",
                "019-3333222", "marcus.tan94@yahoo.com",
                "No. 66, Jalan Padungan, Kuching, Sarawak", "2025-08-29"));

        patientList.insertLast(new Patient("P017", "Aisyah binti Salleh", "030725-10-1122", 8, "Female", // child
                "011-2222334", "aisyah.salleh03@gmail.com",
                "No. 5, Jalan Pantai Dalam, Kuala Lumpur", "2025-08-29"));

        patientList.insertLast(new Patient("P018", "Daniel Wong Jun Hao", "990831-14-7788", 33, "Male",
                "014-5555666", "daniel.wong99@gmail.com",
                "No. 45, Jalan Kebun Teh, Johor Bahru, Johor", "2025-08-29"));

        patientList.insertLast(new Patient("P019", "Thavamani a/l Krishnan", "760129-10-4466", 90, "Male", // very elderly
                "015-1212121", "thavamani.krishnan76@gmail.com",
                "No. 8, Jalan Laksamana, Melaka City, Melaka", "2025-08-29"));

        patientList.insertLast(new Patient("P020", "Cheong Li Fang", "970618-08-5577", 25, "Female",
                "012-9898989", "lifang.cheong97@yahoo.com",
                "No. 10, Jalan Pasir Puteh, Ipoh, Perak", "2025-08-29"));

        return patientList;
    }

    // Initialize Medicine Test Data
    private static DoublyLinkedList<Pair<String, Medicine>> initializeMedicineData() {

        DoublyLinkedList<Pair<String, Medicine>> medList = new DoublyLinkedList<>();

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
        medList.insertLast(new Pair<>("MED020", new Medicine("MED020", "Zinc Oxide Paste", "Sudocrem", "Dermatological", "Paste", "N/A", 70, 3.20)));
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

        return medList;

    }
  
    // Initialize Treatment Types Data
    public static DoublyLinkedList<Pair<String, Double>> getTreatmentTypes() {
        DoublyLinkedList<Pair<String, Double>> treatmentTypes = new DoublyLinkedList<>();
        treatmentTypes.insertLast(new Pair<>("Standard Consultation", 30.00));
        treatmentTypes.insertLast(new Pair<>("Minor Wound Dressing", 50.00));
        treatmentTypes.insertLast(new Pair<>("Vaccination Shot", 80.00));
        treatmentTypes.insertLast(new Pair<>("Blood Test", 120.00));
        treatmentTypes.insertLast(new Pair<>("Specialist Referral", 20.00));
        return treatmentTypes;
    }

    // Initialize Queue Test Data
    private static int lastQueueNumber = 1000; // for generating E1001, E1002, ...

    private static String generateQueueNumber() {
    lastQueueNumber++;
    return "E" + lastQueueNumber;
    }

    private static DoublyLinkedList<Pair<String, QueueEntry>> initializeQueueData(DoublyLinkedList<Patient> patientList) {
    DoublyLinkedList<Pair<String, QueueEntry>> queueList = new DoublyLinkedList<>();
    if (patientList != null && !patientList.isEmpty()) {
        long baseTime = System.currentTimeMillis(); // reference start time
        for (int i = 1; i <= 10 && i <= patientList.getSize(); i++) {
            Patient patient = patientList.getElement(i).getEntry();
            String queueNumber = generateQueueNumber();
            String status = "Done";

            // Enqueue time: staggered randomly 1-5 minutes apart
            long enqueueOffset = (long) (Math.random() * 5 * 60_000L + 60_000L); // 1-5 minutes
            long enqueueMillis = baseTime - enqueueOffset * i;

            // Start consultation: 1-10 minutes after enqueue
            long startOffset = (long) (Math.random() * 10 * 60_000L + 60_000L); // 1-10 minutes
            long startMillis = enqueueMillis + startOffset;

            // End consultation: 5-15 minutes after start
            long consultDuration = (long) (5 * 60_000L + Math.random() * 10 * 60_000L); // 5-15 minutes
            long endMillis = startMillis + consultDuration;

            QueueEntry entry = new QueueEntry(patient, queueNumber, status);
            entry.setEnqueueTime(enqueueMillis);
            entry.setStartConsultTime(startMillis);
            entry.setEndConsultTime(endMillis);
            entry.setConsultDuration(consultDuration);

            // Insert into queue list
            queueList.insertLast(new Pair<>(queueNumber, entry));
        }
    }
    return queueList;
}
    

}
