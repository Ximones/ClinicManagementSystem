package control.ConsultationController;

import adt.DoublyLinkedList;
import adt.Pair;
import adt.Node;
import enitity.Consultation;
import enitity.Appointment;
import enitity.Patient;
import enitity.Doctor;
import enitity.Prescription;
import enitity.PrescriptionItem;
import enitity.Medicine;
import enitity.QueueEntry;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import utility.FileUtils;

/**
 * Control class for managing consultation operations
 *
 * @author Zhen Bang
 */
public class ConsultationControl {

    private DoublyLinkedList<Pair<String, Consultation>> consultationList;
    private DoublyLinkedList<Pair<String, Appointment>> appointmentList;
    private DoublyLinkedList<Pair<String, Prescription>> prescriptionList;
    private DoublyLinkedList<Pair<String, QueueEntry>> queueList;

    public ConsultationControl() {
        this.consultationList = new DoublyLinkedList<>();
        this.appointmentList = new DoublyLinkedList<>();
        this.prescriptionList = new DoublyLinkedList<>();
        this.queueList = new DoublyLinkedList<>();

        // Load existing data from files
        loadData();
    }

    /**
     * Loads all consultation data from files
     */
    private void loadData() {
        // Load consultations
        DoublyLinkedList<Pair<String, Consultation>> loadedConsultations
                = (DoublyLinkedList<Pair<String, Consultation>>) FileUtils.readDataFromFile("consultations");
        if (loadedConsultations != null) {
            this.consultationList = loadedConsultations;
            // Update consultation index to prevent duplicate IDs
            updateConsultationIndex();
        }

        // Load appointments
        DoublyLinkedList<Pair<String, Appointment>> loadedAppointments
                = (DoublyLinkedList<Pair<String, Appointment>>) FileUtils.readDataFromFile("appointments");
        if (loadedAppointments != null) {
            this.appointmentList = loadedAppointments;
            // Update appointment index to prevent duplicate IDs
            updateAppointmentIndex();
        }

        // Load prescriptions
        DoublyLinkedList<Pair<String, Prescription>> loadedPrescriptions
                = (DoublyLinkedList<Pair<String, Prescription>>) FileUtils.readDataFromFile("prescriptions");
        if (loadedPrescriptions != null) {
            this.prescriptionList = loadedPrescriptions;
        }

        // Load queue
        try {
            DoublyLinkedList<Pair<String, QueueEntry>> loadedQueue
                    = (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
            if (loadedQueue != null && !loadedQueue.isEmpty()) {
                this.queueList = loadedQueue;
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            try {
                DoublyLinkedList<QueueEntry> oldFormatQueue
                        = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
                if (oldFormatQueue != null && !oldFormatQueue.isEmpty()) {
                    // Convert old format to new format
                    for (QueueEntry entry : oldFormatQueue) {
                        Pair<String, QueueEntry> pair = new Pair<>(entry.getQueueNumber(), entry);
                        this.queueList.insertLast(pair);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error loading queue data: " + ex.getMessage());
            }
        }
    }

    /**
     * Reloads queue data from persistent storage to reflect external updates
     */
    public void reloadQueue() {
        try {
            DoublyLinkedList<Pair<String, QueueEntry>> loadedQueue
                    = (DoublyLinkedList<Pair<String, QueueEntry>>) FileUtils.readDataFromFile("queue");
            if (loadedQueue != null) {
                this.queueList = loadedQueue;
            }
        } catch (ClassCastException e) {
            try {
                DoublyLinkedList<QueueEntry> oldFormatQueue
                        = (DoublyLinkedList<QueueEntry>) FileUtils.readDataFromFile("queue");
                if (oldFormatQueue != null) {
                    DoublyLinkedList<Pair<String, QueueEntry>> converted = new DoublyLinkedList<>();
                    for (QueueEntry entry : oldFormatQueue) {
                        converted.insertLast(new Pair<>(entry.getQueueNumber(), entry));
                    }
                    this.queueList = converted;
                }
            } catch (Exception ex) {
                System.err.println("Error reloading queue data: " + ex.getMessage());
            }
        }
    }

    /**
     * Saves all consultation data to files
     */
    public void saveData() {
        FileUtils.writeDataToFile("consultations", consultationList);
        FileUtils.writeDataToFile("appointments", appointmentList);
        FileUtils.writeDataToFile("prescriptions", prescriptionList);
        FileUtils.writeDataToFile("queue", queueList);
    }

    // Consultation Management Methods
    /**
     * Creates a new consultation
     *
     * @param patient The patient for the consultation
     * @param doctor The doctor conducting the consultation
     * @param consultationDateTime The date and time of the consultation
     * @param consultationType The type of consultation
     * @param symptoms The patient's symptoms
     * @return The created consultation
     */
    public Consultation createConsultation(Patient patient, Doctor doctor,
            LocalDateTime consultationDateTime,
            String consultationType, String symptoms) {
        Consultation consultation = new Consultation(patient, doctor, consultationDateTime,
                consultationType, symptoms);
        Pair<String, Consultation> consultationPair = new Pair<>(consultation.getConsultationID(), consultation);
        consultationList.insertLast(consultationPair);

        // Save data after creating consultation
        saveData();

        return consultation;
    }

    /**
     * Retrieves a consultation by ID
     *
     * @param consultationID The consultation ID
     * @return The consultation if found, null otherwise
     */
    public Consultation getConsultation(String consultationID) {
        Object result = consultationList.findByKey(consultationID);
        if (result instanceof Consultation) {
            return (Consultation) result;
        }
        return null;
    }

    /**
     * Updates consultation status
     *
     * @param consultationID The consultation ID
     * @param newStatus The new status
     * @return true if updated successfully, false otherwise
     */
    public boolean updateConsultationStatus(String consultationID, String newStatus) {
        Consultation consultation = getConsultation(consultationID);
        if (consultation != null) {
            consultation.setStatus(newStatus);
            // Persist status change immediately
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Updates consultation diagnosis and notes
     *
     * @param consultationID The consultation ID
     * @param diagnosis The diagnosis
     * @param notes Additional notes
     * @return true if updated successfully, false otherwise
     */
    public boolean updateConsultationDiagnosis(String consultationID, String diagnosis, String notes) {
        Consultation consultation = getConsultation(consultationID);
        if (consultation != null) {
            consultation.setDiagnosis(diagnosis);
            consultation.setNotes(notes);
            consultation.setStatus("Completed");
            return true;
        }
        return false;
    }

    /**
     * Deletes a consultation by ID
     *
     * @param consultationID The consultation ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteConsultation(String consultationID) {
        // First, check if there are any related prescriptions
        List<Prescription> relatedPrescriptions = getPrescriptionsByConsultation(consultationID);
        boolean hasRelatedPrescriptions = !relatedPrescriptions.isEmpty();
        
        // Delete the consultation
        for (int i = 1; i <= consultationList.getSize(); i++) {
            Node<Pair<String, Consultation>> node = consultationList.getElement(i);
            if (node != null && node.getEntry().getKey().equals(consultationID)) {
                consultationList.deleteAtPosition(i);
                
                // If there were related prescriptions, delete them too
                if (hasRelatedPrescriptions) {
                    deleteRelatedPrescriptions(consultationID);
                }
                
                saveData();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Deletes all prescriptions related to a consultation
     *
     * @param consultationID The consultation ID
     */
    private void deleteRelatedPrescriptions(String consultationID) {
        for (int i = 1; i <= prescriptionList.getSize(); i++) {
            Node<Pair<String, Prescription>> node = prescriptionList.getElement(i);
            if (node != null && node.getEntry().getValue().getConsultation() != null &&
                node.getEntry().getValue().getConsultation().getConsultationID().equals(consultationID)) {
                prescriptionList.deleteAtPosition(i);
                // Adjust index since we deleted an element
                i--;
            }
        }
    }

    /**
     * Gets all consultations for a specific patient
     *
     * @param patientID The patient ID
     * @return List of consultations for the patient
     */
    public List<Consultation> getConsultationsByPatient(String patientID) {
        List<Consultation> patientConsultations = new ArrayList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            if (consultation.getPatient() != null
                    && consultation.getPatient().getPatientID().equals(patientID)) {
                patientConsultations.add(consultation);
            }
        }

        return patientConsultations;
    }

    /**
     * Gets all consultations for a specific doctor
     *
     * @param doctorID The doctor ID
     * @return List of consultations for the doctor
     */
    public List<Consultation> getConsultationsByDoctor(String doctorID) {
        List<Consultation> doctorConsultations = new ArrayList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            if (consultation.getDoctor() != null
                    && consultation.getDoctor().getDoctorID().equals(doctorID)) {
                doctorConsultations.add(consultation);
            }
        }

        return doctorConsultations;
    }

    /**
     * Gets consultations for a specific date
     *
     * @param date The date to search for
     * @return List of consultations on the specified date
     */
    public List<Consultation> getConsultationsByDate(LocalDate date) {
        List<Consultation> dateConsultations = new ArrayList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            if (consultation.getConsultationDateTime() != null
                    && consultation.getConsultationDateTime().toLocalDate().equals(date)) {
                dateConsultations.add(consultation);
            }
        }

        return dateConsultations;
    }

    /**
     * Gets all consultations
     *
     * @return List of all consultations
     */
    public List<Consultation> getAllConsultations() {
        List<Consultation> allConsultations = new ArrayList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            allConsultations.add(pair.getValue());
        }

        return allConsultations;
    }

    /**
     * Gets consultations by status
     *
     * @param status The status to filter by
     * @return List of consultations with the specified status
     */
    public List<Consultation> getConsultationsByStatus(String status) {
        List<Consultation> statusConsultations = new ArrayList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            if (consultation.getStatus().equals(status)) {
                statusConsultations.add(consultation);
            }
        }

        return statusConsultations;
    }

    // Appointment Management Methods
    /**
     * Creates a follow-up appointment
     *
     * @param patient The patient for the appointment
     * @param doctor The doctor for the appointment
     * @param appointmentDateTime The date and time of the appointment
     * @param appointmentType The type of appointment
     * @param reason The reason for the appointment
     * @param originalConsultation The original consultation
     * @return The created appointment
     */
    public Appointment createAppointment(Patient patient, Doctor doctor,
            LocalDateTime appointmentDateTime,
            String appointmentType, String reason,
            Consultation originalConsultation) {
        Appointment appointment = new Appointment(patient, doctor, appointmentDateTime,
                appointmentType, reason, originalConsultation);
        Pair<String, Appointment> appointmentPair = new Pair<>(appointment.getAppointmentID(), appointment);
        appointmentList.insertLast(appointmentPair);

        // Update the original consultation to indicate it requires follow-up
        if (originalConsultation != null) {
            originalConsultation.setRequiresFollowUp(true);
            originalConsultation.setFollowUpDateTime(appointmentDateTime);
        }

        // Save data after creating appointment
        saveData();

        return appointment;
    }

    /**
     * Retrieves an appointment by ID
     *
     * @param appointmentID The appointment ID
     * @return The appointment if found, null otherwise
     */
    public Appointment getAppointment(String appointmentID) {
        Object result = appointmentList.findByKey(appointmentID);
        if (result instanceof Appointment) {
            return (Appointment) result;
        }
        return null;
    }

    /**
     * Updates appointment status
     *
     * @param appointmentID The appointment ID
     * @param newStatus The new status
     * @return true if updated successfully, false otherwise
     */
    public boolean updateAppointmentStatus(String appointmentID, String newStatus) {
        Appointment appointment = getAppointment(appointmentID);
        if (appointment != null) {
            appointment.setStatus(newStatus);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Gets all appointments for today
     *
     * @return List of today's appointments
     */
    public List<Appointment> getTodayAppointments() {
        List<Appointment> todayAppointments = new ArrayList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            Appointment appointment = pair.getValue();
            if (appointment.isToday()) {
                todayAppointments.add(appointment);
            }
        }

        return todayAppointments;
    }

    /**
     * Gets upcoming appointments
     *
     * @return List of upcoming appointments
     */
    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> upcomingAppointments = new ArrayList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            Appointment appointment = pair.getValue();
            if (appointment.isUpcoming()) {
                upcomingAppointments.add(appointment);
            }
        }

        return upcomingAppointments;
    }

    /**
     * Gets appointments for a specific patient
     *
     * @param patientID The patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(String patientID) {
        List<Appointment> patientAppointments = new ArrayList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            Appointment appointment = pair.getValue();
            if (appointment.getPatient() != null
                    && appointment.getPatient().getPatientID().equals(patientID)) {
                patientAppointments.add(appointment);
            }
        }

        return patientAppointments;
    }

    /**
     * Gets appointments for a specific doctor
     *
     * @param doctorID The doctor ID
     * @return List of appointments for the doctor
     */
    public List<Appointment> getAppointmentsByDoctor(String doctorID) {
        List<Appointment> doctorAppointments = new ArrayList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            Appointment appointment = pair.getValue();
            if (appointment.getDoctor() != null
                    && appointment.getDoctor().getDoctorID().equals(doctorID)) {
                doctorAppointments.add(appointment);
            }
        }

        return doctorAppointments;
    }

    /**
     * Gets all appointments
     *
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> allAppointments = new ArrayList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            allAppointments.add(pair.getValue());
        }

        return allAppointments;
    }

    // Statistics and Reporting Methods
    /**
     * Gets consultation count by type
     *
     * @return Map of consultation types and their counts
     */
    public DoublyLinkedList<Pair<String, Integer>> getConsultationTypeCounts() {
        DoublyLinkedList<Pair<String, Integer>> typeCounts = new DoublyLinkedList<>();

        for (Pair<String, Consultation> pair : consultationList) {
            Consultation consultation = pair.getValue();
            String type = consultation.getConsultationType();

            // Find existing count for this type
            Object existingCount = typeCounts.findByKey(type);
            if (existingCount != null) {
                int count = (Integer) existingCount;
                // Remove old pair and add new one with updated count
                for (int i = 1; i <= typeCounts.getSize(); i++) {
                    Pair<String, Integer> countPair = typeCounts.getElement(i).getEntry();
                    if (countPair.getKey().equals(type)) {
                        typeCounts.deleteAtPosition(i);
                        typeCounts.insertLast(new Pair<>(type, count + 1));
                        break;
                    }
                }
            } else {
                typeCounts.insertLast(new Pair<>(type, 1));
            }
        }

        return typeCounts;
    }

    /**
     * Gets appointment count by type
     *
     * @return Map of appointment types and their counts
     */
    public DoublyLinkedList<Pair<String, Integer>> getAppointmentTypeCounts() {
        DoublyLinkedList<Pair<String, Integer>> typeCounts = new DoublyLinkedList<>();

        for (Pair<String, Appointment> pair : appointmentList) {
            Appointment appointment = pair.getValue();
            String type = appointment.getAppointmentType();

            // Find existing count for this type
            Object existingCount = typeCounts.findByKey(type);
            if (existingCount != null) {
                int count = (Integer) existingCount;
                // Remove old pair and add new one with updated count
                for (int i = 1; i <= typeCounts.getSize(); i++) {
                    Pair<String, Integer> countPair = typeCounts.getElement(i).getEntry();
                    if (countPair.getKey().equals(type)) {
                        typeCounts.deleteAtPosition(i);
                        typeCounts.insertLast(new Pair<>(type, count + 1));
                        break;
                    }
                }
            } else {
                typeCounts.insertLast(new Pair<>(type, 1));
            }
        }

        return typeCounts;
    }

    // Prescription Management Methods
    /**
     * Creates a new prescription for a consultation
     *
     * @param consultation The consultation for the prescription
     * @param diagnosis The diagnosis
     * @param instructions The prescription instructions
     * @return The created prescription
     */
    public Prescription createPrescription(Consultation consultation, String diagnosis, String instructions) {
        Prescription prescription = new Prescription(consultation, diagnosis, instructions);
        Pair<String, Prescription> prescriptionPair = new Pair<>(prescription.getPrescriptionID(), prescription);
        prescriptionList.insertLast(prescriptionPair);
        saveData();
        return prescription;
    }

    /**
     * Adds a prescription item to a prescription
     *
     * @param prescriptionID The prescription ID
     * @param medicine The medicine to prescribe
     * @param quantity The quantity
     * @param dosage The dosage
     * @param frequency The frequency
     * @param duration The duration
     * @return true if added successfully, false otherwise
     */
    public boolean addPrescriptionItem(String prescriptionID, Medicine medicine, int quantity,
            String dosage, String frequency, String duration) {
        Prescription prescription = getPrescription(prescriptionID);
        if (prescription != null) {
            PrescriptionItem item = new PrescriptionItem(medicine, quantity, dosage, frequency, duration);
            prescription.addPrescriptionItem(item);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Retrieves a prescription by ID
     *
     * @param prescriptionID The prescription ID
     * @return The prescription if found, null otherwise
     */
    public Prescription getPrescription(String prescriptionID) {
        Object result = prescriptionList.findByKey(prescriptionID);
        if (result instanceof Prescription) {
            return (Prescription) result;
        }
        return null;
    }

    /**
     * Gets all prescriptions for a specific patient
     *
     * @param patientID The patient ID
     * @return List of prescriptions for the patient
     */
    public List<Prescription> getPrescriptionsByPatient(String patientID) {
        List<Prescription> patientPrescriptions = new ArrayList<>();

        for (Pair<String, Prescription> pair : prescriptionList) {
            Prescription prescription = pair.getValue();
            if (prescription.getPatient() != null
                    && prescription.getPatient().getPatientID().equals(patientID)) {
                patientPrescriptions.add(prescription);
            }
        }

        return patientPrescriptions;
    }

    /**
     * Gets all prescriptions for a specific consultation
     *
     * @param consultationID The consultation ID
     * @return List of prescriptions for the consultation
     */
    public List<Prescription> getPrescriptionsByConsultation(String consultationID) {
        List<Prescription> consultationPrescriptions = new ArrayList<>();

        for (Pair<String, Prescription> pair : prescriptionList) {
            Prescription prescription = pair.getValue();
            if (prescription.getConsultation() != null
                    && prescription.getConsultation().getConsultationID().equals(consultationID)) {
                consultationPrescriptions.add(prescription);
            }
        }

        return consultationPrescriptions;
    }

    /**
     * Gets all prescriptions
     *
     * @return DoublyLinkedList of all prescriptions with their IDs
     */
    public DoublyLinkedList<Pair<String, Prescription>> getAllPrescriptions() {
        return prescriptionList;
    }
    
    /**
     * Deletes a prescription by ID
     *
     * @param prescriptionID The prescription ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deletePrescription(String prescriptionID) {
        for (int i = 1; i <= prescriptionList.getSize(); i++) {
            Node<Pair<String, Prescription>> node = prescriptionList.getElement(i);
            if (node != null && node.getEntry().getKey().equals(prescriptionID)) {
                prescriptionList.deleteAtPosition(i);
                saveData();
                return true;
            }
        }
        return false;
    }

    public void reloadPrescriptions() {
        DoublyLinkedList<Pair<String, Prescription>> loadedPrescriptions
                = (DoublyLinkedList<Pair<String, Prescription>>) FileUtils.readDataFromFile("prescriptions");
        if (loadedPrescriptions != null) {
            this.prescriptionList = loadedPrescriptions;
        }
    }

    /**
     * Updates the consultation index to prevent duplicate IDs
     */
    private void updateConsultationIndex() {
        if (consultationList.isEmpty()) {
            Consultation.setConsultationIndex(0);
            return;
        }
        
        int maxIndex = 0;
        for (Pair<String, Consultation> pair : consultationList) {
            String consultationID = pair.getKey();
            if (consultationID.startsWith("C")) {
                try {
                    int index = Integer.parseInt(consultationID.substring(1));
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        Consultation.setConsultationIndex(maxIndex);
    }

    /**
     * Updates the appointment index to prevent duplicate IDs
     */
    private void updateAppointmentIndex() {
        if (appointmentList.isEmpty()) {
            enitity.Appointment.setAppointmentIndex(0);
            return;
        }
        int maxIndex = 0;
        for (Pair<String, Appointment> pair : appointmentList) {
            String appointmentID = pair.getKey();
            if (appointmentID.startsWith("A")) {
                try {
                    int index = Integer.parseInt(appointmentID.substring(1));
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        enitity.Appointment.setAppointmentIndex(maxIndex);
    }

    /**
     * Reloads consultations from storage to reflect external changes
     */
    public void reloadConsultations() {
        DoublyLinkedList<Pair<String, Consultation>> loadedConsultations
                = (DoublyLinkedList<Pair<String, Consultation>>) FileUtils.readDataFromFile("consultations");
        if (loadedConsultations != null) {
            this.consultationList = loadedConsultations;
            // Update consultation index to prevent duplicate IDs
            updateConsultationIndex();
        }
    }

    // Getters for the lists
    public DoublyLinkedList<Pair<String, Consultation>> getConsultationList() {
        return consultationList;
    }

    public DoublyLinkedList<Pair<String, Appointment>> getAppointmentList() {
        return appointmentList;
    }

    public DoublyLinkedList<Pair<String, Prescription>> getPrescriptionList() {
        return prescriptionList;
    }

    // Queue Management Methods
    /**
     * Adds a patient to the consultation queue
     *
     * @param patient The patient to add to queue
     * @return The created queue entry
     */
    public QueueEntry addToQueue(Patient patient) {
        String queueNumber = generateQueueNumber();
        QueueEntry queueEntry = new QueueEntry(patient, queueNumber, "Waiting");
        Pair<String, QueueEntry> queuePair = new Pair<>(queueNumber, queueEntry);
        queueList.insertLast(queuePair);
        saveData();
        return queueEntry;
    }

    /**
     * Gets the next patient from the queue
     *
     * @return The next queue entry, or null if queue is empty
     */
    public QueueEntry getNextPatient() {
        if (!queueList.isEmpty()) {
            Node<Pair<String, QueueEntry>> firstNode = queueList.getFirst();
            if (firstNode != null) {
                QueueEntry entry = firstNode.getEntry().getValue();
                entry.markStartConsult();
                saveData();
                return entry;
            }
        }
        return null;
    }

    /**
     * Marks a patient as consulting (moved from queue to consultation)
     *
     * @param queueNumber The queue number of the patient
     * @return true if marked successfully, false otherwise
     */
    public boolean markPatientConsulting(String queueNumber) {
        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                if (entry.getQueueNumber().equals(queueNumber)) {
                    entry.markConsulting();
                    saveData();
                    return true;
                }
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    if (entry.getQueueNumber().equals(queueNumber)) {
                        entry.markConsulting();
                        saveData();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Completes a consultation and removes patient from queue
     *
     * @param queueNumber The queue number of the patient
     * @return true if completed successfully, false otherwise
     */
    public boolean completeConsultation(String queueNumber) {
        for (Pair<String, QueueEntry> pair : queueList) {
        QueueEntry entry = pair.getValue();
           if (entry.getQueueNumber().equals(queueNumber)) {
            entry.markConsultationDone();  // ✅ only mark Done
            saveData();                   // ✅ keep in queue file
            return true;
        }
    }
    return false;
}

    /**
     * Gets the current consulting patient (first patient with "Consulting" status)
     *
     * @return The consulting queue entry, or null if none
     */
    public QueueEntry getCurrentConsultingPatient() {
        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                if ("Consulting".equals(entry.getStatus())) {
                    return entry;
                }
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    if ("Consulting".equals(entry.getStatus())) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes a patient from the queue
     *
     * @param queueNumber The queue number to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeFromQueue(String queueNumber) {
        // Find and remove the queue entry
        for (int i = 1; i <= queueList.getSize(); i++) {
            Node<Pair<String, QueueEntry>> node = queueList.getElement(i);
            if (node != null && node.getEntry().getKey().equals(queueNumber)) {
                queueList.deleteAtPosition(i);
                saveData();
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all patients in the queue
     *
     * @return List of all queue entries
     */
    public List<QueueEntry> getAllQueueEntries() {
        List<QueueEntry> allEntries = new ArrayList<>();

        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                allEntries.add(pair.getValue());
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    allEntries.add((QueueEntry) obj);
                }
            }
        }

        return allEntries;
    }

    /**
     * Gets the current queue position for a patient
     *
     * @param patientID The patient ID
     * @return The queue position (1-based), or -1 if not found
     */
    public int getQueuePosition(String patientID) {
        int position = 1;
        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                if (entry.getPatient() != null
                        && entry.getPatient().getPatientID().equals(patientID)) {
                    return position;
                }
                position++;
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    if (entry.getPatient() != null
                            && entry.getPatient().getPatientID().equals(patientID)) {
                        return position;
                    }
                    position++;
                }
            }
        }
        return -1;
    }

    /**
     * Gets the average waiting time in the queue
     *
     * @return Average waiting time in milliseconds
     */
    public long getAverageWaitingTime() {
        if (queueList.isEmpty()) {
            return 0;
        }

        long totalWaitingTime = 0;
        int count = 0;

        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                totalWaitingTime += entry.getWaitingTimeMillis();
                count++;
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    totalWaitingTime += entry.getWaitingTimeMillis();
                    count++;
                }
            }
        }

        return count > 0 ? totalWaitingTime / count : 0;
    }

    private String generateQueueNumber() {
        return "Q" + String.format("%03d", queueList.getSize() + 1);
    }

    public DoublyLinkedList<Pair<String, QueueEntry>> getQueueList() {
        // If the queue list contains direct QueueEntry objects, convert them to Pair format
        if (!queueList.isEmpty()) {
            try {
                // Test if it's already in Pair format
                Object first = queueList.getElement(1).getEntry();
                if (!(first instanceof Pair)) {
                    // Convert old format to new format
                    DoublyLinkedList<Pair<String, QueueEntry>> convertedList = new DoublyLinkedList<>();
                    for (Object obj : queueList) {
                        if (obj instanceof QueueEntry) {
                            QueueEntry entry = (QueueEntry) obj;
                            Pair<String, QueueEntry> pair = new Pair<>(entry.getQueueNumber(), entry);
                            convertedList.insertLast(pair);
                        }
                    }
                    this.queueList = convertedList;
                }
            } catch (Exception e) {
                // If conversion fails, return empty list
                return new DoublyLinkedList<>();
            }
        }
        return queueList;
    }

    /**
     * Marks a patient as prescriptioning after consultation
     *
     * @param queueNumber The queue number of the patient
     * @return true if marked successfully, false otherwise
     */
    public boolean markPatientPrescriptioning(String queueNumber) {
        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                if (entry.getQueueNumber().equals(queueNumber)) {
                    entry.markPrescriptioning();
                    saveData();
                    return true;
                }
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    if (entry.getQueueNumber().equals(queueNumber)) {
                        entry.markPrescriptioning();
                        saveData();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Completes the entire consultation and prescription process and removes patient from queue
     *
     * @param queueNumber The queue number of the patient
     * @return true if completed successfully, false otherwise
     */
    public boolean completeConsultationAndPrescription(String queueNumber) {
         for (Pair<String, QueueEntry> pair : queueList) {
        QueueEntry entry = pair.getValue();
          if (entry.getQueueNumber().equals(queueNumber)) {
            entry.markConsultationDone();  // ✅ only mark Done
            saveData();
            return true;
        }
      }
    return false;
    }


    /**
     * Gets the current prescriptioning patient (first patient with "Prescriptioning" status)
     *
     * @return The prescriptioning queue entry, or null if none
     */
    public QueueEntry getCurrentPrescriptioningPatient() {
        try {
            // Try new format (Pair<String, QueueEntry>)
            for (Pair<String, QueueEntry> pair : queueList) {
                QueueEntry entry = pair.getValue();
                if ("Prescribing".equals(entry.getStatus())) {
                    return entry;
                }
            }
        } catch (ClassCastException e) {
            // Handle old format (direct QueueEntry objects)
            for (Object obj : queueList) {
                if (obj instanceof QueueEntry) {
                    QueueEntry entry = (QueueEntry) obj;
                    if ("Prescribing".equals(entry.getStatus())) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

}
