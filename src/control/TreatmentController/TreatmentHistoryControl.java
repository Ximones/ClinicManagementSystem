package control.TreatmentController;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.MedicalTreatmentManagementUI.TreatmentHistoryPanel;
import enitity.Treatment;
import utility.ReportGenerator;

/**
 * Control class for TreatmentHistoryPanel. Handles logic for displaying, filtering,
 * sorting, and reporting on treatment history following the ECB architectural pattern.
 * 
 * @author Tan Jun Yew
 */
public class TreatmentHistoryControl {

    private final TreatmentHistoryPanel view; // The panel this controller manages
    private final TreatmentControl treatmentControl; // The main treatment control

    /**
     * Constructor for TreatmentHistoryControl.
     *
     * @param view The TreatmentHistoryPanel instance.
     * @param treatmentControl The main TreatmentControl instance.
     */
    public TreatmentHistoryControl(TreatmentHistoryPanel view, TreatmentControl treatmentControl) {
        this.view = view;
        this.treatmentControl = treatmentControl;
    }

    /**
     * Loads all treatments from the file and returns them for display.
     * @return A list of all treatments.
     */
    public DoublyLinkedList<Pair<String, Treatment>> loadAllTreatments() {
        return treatmentControl.getAllTreatments();
    }

    /**
     * Filters treatments based on search criteria.
     * @param query The search query.
     * @return A filtered list of treatments.
     */
    public DoublyLinkedList<Pair<String, Treatment>> filterTreatments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return loadAllTreatments();
        }

        // Always use latest data from file to include newly added treatments
        TreatmentControl freshControl = new TreatmentControl();

        DoublyLinkedList<Pair<String, Treatment>> searchResults = new DoublyLinkedList<>();
        
        // Search in Patient Name
        DoublyLinkedList<Pair<String, Treatment>> patientResults = freshControl.filterTreatments("Patient Name", query);
        for (Pair<String, Treatment> pair : patientResults) {
            searchResults.insertLast(pair);
        }
        
        // Search in Doctor Name
        DoublyLinkedList<Pair<String, Treatment>> doctorResults = freshControl.filterTreatments("Doctor Name", query);
        for (Pair<String, Treatment> pair : doctorResults) {
            // Avoid duplicates
            boolean exists = false;
            for (Pair<String, Treatment> existing : searchResults) {
                if (existing.getKey().equals(pair.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                searchResults.insertLast(pair);
            }
        }
        
        // Search in Diagnosis
        DoublyLinkedList<Pair<String, Treatment>> diagnosisResults = freshControl.filterTreatments("Diagnosis", query);
        for (Pair<String, Treatment> pair : diagnosisResults) {
            // Avoid duplicates
            boolean exists = false;
            for (Pair<String, Treatment> existing : searchResults) {
                if (existing.getKey().equals(pair.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                searchResults.insertLast(pair);
            }
        }
        
        return searchResults;
    }

    /**
     * Sorts treatments based on the specified criteria.
     * @param sortBy The attribute to sort by ("Date" or "Cost").
     * @param order The order ("ASC" or "DESC").
     * @param treatments The list of treatments to sort.
     * @return A sorted list of treatments.
     */
    public DoublyLinkedList<Pair<String, Treatment>> sortTreatments(String sortBy, String order, DoublyLinkedList<Pair<String, Treatment>> treatments) {
        return treatmentControl.bubbleSortTreatments(treatments, sortBy, order);
    }

    public static class OperationResult {
        public final boolean success;
        public final String message;
        public OperationResult(boolean success, String message) { this.success = success; this.message = message; }
    }

    /**
     * Generates a common diagnoses report.
     */
    public OperationResult generateDiagnosisFrequencyReport() {
        TreatmentControl freshControl = new TreatmentControl();
        DoublyLinkedList<Pair<String, Integer>> frequencyData = freshControl.getDiagnosisFrequency();
        if (frequencyData == null || frequencyData.isEmpty()) {
            return new OperationResult(false, "No treatment data to generate report.");
        }
        ReportGenerator.generateDiagnosisFrequencyReport(frequencyData);
        return new OperationResult(true, "Report generated: 'Common_Diagnoses_Report.pdf'");
    }

    /**
     * Generates a patient history report for a specific patient.
     * @param patientId The ID of the patient.
     */
    public OperationResult generatePatientHistoryReport(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return new OperationResult(false, "Patient ID is required.");
        }
        String finalPatientId = patientId.trim().toUpperCase();
        TreatmentControl freshControl = new TreatmentControl();
        DoublyLinkedList<Treatment> patientHistory = freshControl.getTreatmentsForPatient(finalPatientId);
        if (patientHistory == null || patientHistory.isEmpty()) {
            return new OperationResult(false, "No treatments found for Patient ID: " + finalPatientId);
        }
        ReportGenerator.generatePatientHistoryReport(patientHistory);
        return new OperationResult(true, "Report generated: 'Patient_History_" + finalPatientId + ".pdf'");
    }

    /**
     * Generates a treatment type popularity report.
     * @param treatments The list of treatments to analyze.
     */
    public OperationResult generateTreatmentTypePopularityReport(DoublyLinkedList<Pair<String, Treatment>> treatments) {
        if (treatments == null || treatments.isEmpty()) {
            return new OperationResult(false, "No treatment data to generate report.");
        }
        ReportGenerator.generateTreatmentTypePopularityReport(treatments);
        return new OperationResult(true, "Report generated: 'Treatment_Type_Popularity_Report.pdf'");
    }
}
