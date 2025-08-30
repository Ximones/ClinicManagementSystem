/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.TreatmentController;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Consultation;
import enitity.Treatment;
import utility.FileUtils;

/**
 *
 * @author User
 */
public class TreatmentControl {

    private DoublyLinkedList<Pair<String, Treatment>> treatmentList;
    private DoublyLinkedList<Treatment> recentTreatmentsList; 

    public TreatmentControl() {
        this.treatmentList = (DoublyLinkedList<Pair<String, Treatment>>) FileUtils.readDataFromFile("treatments");
        if (this.treatmentList == null) {
            this.treatmentList = new DoublyLinkedList<>();
        }
        
        // Initialize the list for this session
        this.recentTreatmentsList = new DoublyLinkedList<>();
        
        if (!treatmentList.isEmpty()) {
            int maxId = 0;
            for (Pair<String, Treatment> pair : treatmentList) {
                String idStr = pair.getKey().replaceAll("[^0-9]", "");
                if (!idStr.isEmpty()) {
                    maxId = Math.max(maxId, Integer.parseInt(idStr));
                }
            }
            Treatment.setTreatmentIndex(maxId);
        }
    }

    /**
     * Creates a new Treatment record, saves it, and pushes it to the recent stack.
     * @param consultation The consultation this treatment is linked to.
     * @param diagnosis The diagnosis made by the doctor.
     * @param details A description of the treatment.
     * @param cost The cost of the treatment.
     * @param notes Any additional notes.
     */
    public void addTreatment(Consultation consultation, String diagnosis, String details, double cost, String notes) {
        Treatment newTreatment = new Treatment(consultation, diagnosis, details, cost, notes);
        Pair<String, Treatment> treatmentPair = new Pair<>(newTreatment.getTreatmentID(), newTreatment);
        treatmentList.insertLast(treatmentPair);
        FileUtils.writeDataToFile("treatments", treatmentList);
        
        // SIMULATE PUSH: Add to the front of the list to act like a stack
        recentTreatmentsList.insertFirst(newTreatment);
    }

    /**
     * Retrieves the entire list of treatments.
     * @return A DoublyLinkedList of all treatment records.
     */
    public DoublyLinkedList<Pair<String, Treatment>> getAllTreatments() {
        return treatmentList;
    }
    
    /**
     * Retrieves the stack of recently added treatments.
     * @return The stack of recent treatments.
     */
     public DoublyLinkedList<Treatment> getRecentTreatmentsList() {
        return recentTreatmentsList;
    }
    
    /**
     * Sorts a list of treatments using the Bubble Sort algorithm.
     * @param list The list of treatments to sort.
     * @param sortBy The attribute to sort by ("Date" or "Cost").
     * @param order The order ("ASC" or "DESC").
     * @return A new, sorted DoublyLinkedList.
     */
    public DoublyLinkedList<Pair<String, Treatment>> bubbleSortTreatments(DoublyLinkedList<Pair<String, Treatment>> list, String sortBy, String order) {
        // Create a copy to avoid modifying the original list
        DoublyLinkedList<Pair<String, Treatment>> sortedList = new DoublyLinkedList<>();
        for (Pair<String, Treatment> p : list) {
            sortedList.insertLast(p);
        }

        int n = sortedList.getSize();
        if (n < 2) {
            return sortedList;
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 1; j <= n - i - 1; j++) {
                Pair<String, Treatment> pair1 = sortedList.getElement(j).getEntry();
                Pair<String, Treatment> pair2 = sortedList.getElement(j + 1).getEntry();
                Treatment t1 = pair1.getValue();
                Treatment t2 = pair2.getValue();

                boolean shouldSwap = false;
                int comparison = 0;

                if ("Date".equals(sortBy)) {
                    comparison = t1.getTreatmentDateTime().compareTo(t2.getTreatmentDateTime());
                } else { // Cost
                    comparison = Double.compare(t1.getCost(), t2.getCost());
                }

                if ("ASC".equals(order) && comparison > 0) {
                    shouldSwap = true;
                } else if ("DESC".equals(order) && comparison < 0) {
                    shouldSwap = true;
                }

                if (shouldSwap) {
                    // Swap the pairs in the linked list
                    sortedList.replace(j, pair2);
                    sortedList.replace(j + 1, pair1);
                }
            }
        }
        return sortedList;
    }

    /**
     * Counts the frequency of each diagnosis using a DoublyLinkedList of Pairs
     * to simulate a map.
     * @return A list of pairs, where each pair is (Diagnosis, Count).
     */
    public DoublyLinkedList<Pair<String, Integer>> getDiagnosisFrequency() {
        DoublyLinkedList<Pair<String, Integer>> frequencyList = new DoublyLinkedList<>();
        
        for (Pair<String, Treatment> pair : treatmentList) {
            String diagnosis = pair.getValue().getDiagnosis();
            boolean found = false;

            // Iterate through the frequency list to find if the diagnosis already exists
            for (Pair<String, Integer> freqPair : frequencyList) {
                if (freqPair.getKey().equals(diagnosis)) {
                    // If found, increment the count
                    freqPair.setValue(freqPair.getValue() + 1);
                    found = true;
                    break;
                }
            }

            // If not found after checking the whole list, add it as a new entry
            if (!found) {
                frequencyList.insertLast(new Pair<>(diagnosis, 1));
            }
        }
        return frequencyList;
    }

    /**
     * Filters the master treatment list to find all records for a specific patient.
     * @param patientId The ID of the patient to search for.
     * @return A list of treatments for the specified patient.
     */
    public DoublyLinkedList<Treatment> getTreatmentsForPatient(String patientId) {
        DoublyLinkedList<Treatment> patientHistory = new DoublyLinkedList<>();
        for (Pair<String, Treatment> pair : treatmentList) {
            Treatment t = pair.getValue();
            if (t.getConsultation().getPatient().getPatientID().equals(patientId)) {
                patientHistory.insertLast(t);
            }
        }
        return patientHistory;
    }
    
    /**
     * Finds a specific treatment by its ID.
     * @param treatmentId The ID of the treatment to find.
     * @return The Treatment object if found, otherwise null.
     */
    public Treatment findTreatmentById(String treatmentId) {
        for (Pair<String, Treatment> pair : treatmentList) {
            if (pair.getKey().equalsIgnoreCase(treatmentId)) {
                return pair.getValue();
            }
        }
        return null;
    }

    /**
     * Saves the entire (potentially modified) treatment list back to the file.
     * This is called after an edit is made in the dialog.
     */
    public void updateTreatment() {
        FileUtils.writeDataToFile("treatments", treatmentList);
    }
}

