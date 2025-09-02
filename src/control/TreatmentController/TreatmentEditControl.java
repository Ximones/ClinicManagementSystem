package control.TreatmentController;

import adt.DoublyLinkedList;
import adt.Pair;
import enitity.Treatment;
import utility.InitializeDataUtils;

/**
 * Control class for TreatmentEditDialog. Handles logic for searching and updating
 * treatment information following the ECB architectural pattern.
 * 
 * @author Tan Jun Yew
 */
public class TreatmentEditControl {

    private final TreatmentControl treatmentControl; // Kept for compatibility if needed elsewhere
    private String editingTreatmentId = null; // Track which ID is being edited

    /**
     * Constructor for TreatmentEditControl.
     *
     * @param view The TreatmentEditDialog instance.
     * @param treatmentControl The main TreatmentControl instance.
     */
    public TreatmentEditControl(TreatmentControl treatmentControl) {
        this.treatmentControl = treatmentControl;
    }

    /**
     * Searches for a treatment by ID using the treatment control.
     * Updates the view to display the treatment's info or an error message.
     *
     * @param treatmentId The ID to search for.
     */
    public Treatment searchTreatmentById(String treatmentId) {
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            return null;
        }

        TreatmentControl freshControl = new TreatmentControl();
        Treatment found = freshControl.findTreatmentById(treatmentId.trim().toUpperCase());
        if (found != null) {
            this.editingTreatmentId = found.getTreatmentID();
        } else {
            this.editingTreatmentId = null;
        }
        return found;
    }

    /**
     * Validates and saves the updated treatment information back to the treatment control
     * and persists the changes to a file.
     */
    public static class OperationResult {
        public final boolean success;
        public final String message;
        public OperationResult(boolean success, String message) { this.success = success; this.message = message; }
    }

    public OperationResult saveTreatmentChanges(String diagnosis, String treatmentDetails, double cost, String notes) {
        if (editingTreatmentId == null || editingTreatmentId.trim().isEmpty()) {
            return new OperationResult(false, "Please search for a treatment first.");
        }

        try {
            if (diagnosis == null || diagnosis.trim().isEmpty()) {
                return new OperationResult(false, "Diagnosis cannot be empty.");
            }
            if (treatmentDetails == null || treatmentDetails.trim().isEmpty()) {
                return new OperationResult(false, "Please select a treatment type.");
            }

            TreatmentControl freshControl = new TreatmentControl();
            Treatment target = freshControl.findTreatmentById(editingTreatmentId);
            if (target == null) {
                return new OperationResult(false, "The treatment no longer exists (it may have been removed).");
            }

            target.setDiagnosis(diagnosis);
            target.setTreatmentDetails(treatmentDetails);
            target.setCost(cost);
            target.setNotes(notes);

            freshControl.updateTreatment();
            return new OperationResult(true, "Treatment " + editingTreatmentId + " updated successfully.");
        } catch (Exception e) {
            return new OperationResult(false, "An error occurred while saving: " + e.getMessage());
        }
    }

    /**
     * Gets the available treatment types from the utility class.
     * @return A list of treatment types with their costs.
     */
    public DoublyLinkedList<Pair<String, Double>> getTreatmentTypes() {
        return InitializeDataUtils.getTreatmentTypes();
    }
}
