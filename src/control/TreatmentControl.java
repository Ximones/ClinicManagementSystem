/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListStack;
import adt.Pair;
import adt.StackInterface;
import enitity.Consultation;
import enitity.Treatment;
import utility.FileUtils;

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
}

