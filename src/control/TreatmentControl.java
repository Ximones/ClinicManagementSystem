/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListMap;
import adt.MapInterface;
import adt.ListStack;
import adt.Pair;
import adt.StackInterface;
import enitity.Consultation;
import enitity.Treatment;
import utility.FileUtils;
import adt.ListMap;

/**
 *
 * @author User
 */
public class TreatmentControl {

    private DoublyLinkedList<Pair<String, Treatment>> treatmentList;
    private StackInterface<Treatment> recentTreatmentsStack; // STACK ADDED HERE

    public TreatmentControl() {
        // Load existing treatments from file when the control is created
        this.treatmentList = (DoublyLinkedList<Pair<String, Treatment>>) FileUtils.readDataFromFile("treatments");
        if (this.treatmentList == null) {
            this.treatmentList = new DoublyLinkedList<>();
        }
        
        // Initialize the stack for this session
        this.recentTreatmentsStack = new ListStack<>();
        
        // After loading, find the highest ID to set the static counter
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
        // Create a new Treatment object
        Treatment newTreatment = new Treatment(consultation, diagnosis, details, cost, notes);

        // Create a Pair to store in the list
        Pair<String, Treatment> treatmentPair = new Pair<>(newTreatment.getTreatmentID(), newTreatment);

        // Add to the list and save to file
        treatmentList.insertLast(treatmentPair);
        FileUtils.writeDataToFile("treatments", treatmentList);
        
        // PUSH TO STACK
        recentTreatmentsStack.push(newTreatment);
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
    public StackInterface<Treatment> getRecentTreatmentsStack() {
        return recentTreatmentsStack;
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
     * Counts the frequency of each diagnosis using a Map ADT.
     * @return A list of pairs, where each pair is (Diagnosis, Count).
     */
    public DoublyLinkedList<Pair<String, Integer>> getDiagnosisFrequency() {
        MapInterface<String, Integer> frequencyMap = new ListMap<>();
        for (Pair<String, Treatment> pair : treatmentList) {
            String diagnosis = pair.getValue().getDiagnosis();
            Integer count = frequencyMap.getValue(diagnosis);
            if (count == null) {
                frequencyMap.add(diagnosis, 1);
            } else {
                frequencyMap.add(diagnosis, count + 1);
            }
        }
        // Convert the map back to a list for the report generator
        return ((ListMap<String, Integer>) frequencyMap).getPairList();
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

