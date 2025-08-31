package control.DoctorManagementController;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.DoctorManagementUI.DoctorAddDialog;
import boundary.DoctorManagementUI.DoctorEditDialog;
import boundary.DoctorManagementUI.DoctorInformationPanel;
import boundary.MainFrame;
import enitity.Doctor;
import javax.swing.JOptionPane;
import utility.FileUtils;
import utility.ReportGenerator;

/**
 *
 * @author Chok Chun Fai
 */
/**
 * The main controller for the Doctor Information module. It manages the data
 * (masterDoctorList) and acts as a bridge between the DoctorInformationPanel
 * (the view) and the underlying business logic.
 */
public class DoctorInformationControl {

    private DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;
    private final DoctorInformationPanel view; // A reference to the UI panel it controls
    private final MainFrame mainFrame; // A reference to the main application frame for navigation

    /**
     * Constructor for the controller.
     *
     * @param view The DoctorInformationPanel UI.
     * @param mainFrame The main application window.
     */
    public DoctorInformationControl(DoctorInformationPanel view, MainFrame mainFrame) {
        this.view = view;
        this.mainFrame = mainFrame;
        loadInitialData();
    }

    /**
     * Provides the master list of doctors to the view.
     *
     * @return The complete list of doctors.
     */
    public DoublyLinkedList<Pair<String, Doctor>> getMasterDoctorList() {
        return masterDoctorList;
    }

    /**
     * Loads the initial doctor data from the file using FileUtils.
     */
    private void loadInitialData() {
        masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (masterDoctorList == null) {
            masterDoctorList = new DoublyLinkedList<>();
        }
        // Set the static index in the Doctor class based on the loaded list size
        if (!masterDoctorList.isEmpty()) {
            Doctor.setDoctorIndex(masterDoctorList.getSize());
        }
    }

    /**
     * Handles the logic for adding a new doctor. It opens the Add dialog, waits
     * for the result, and then updates the master list and view if successful.
     */
    public void addNewDoctor() {
        DoctorAddDialog dialog = new DoctorAddDialog(mainFrame, true);
        dialog.setVisible(true);  // This call is modal, it will pause here until the dialog is closed

        // Retrieve the result from the dialog after it closes
        Pair<String, Doctor> newDoctorPair = dialog.getResult();
        if (newDoctorPair != null) { // Check if the user clicked "Save"
            masterDoctorList.insertLast(newDoctorPair);
            FileUtils.writeDataToFile("doctors", masterDoctorList); // Persist the change
            view.populateDoctorTable(masterDoctorList); // Update the view
        }
    }

    /**
     * Handles the logic for editing an existing doctor. Opens the Edit dialog
     * and refreshes the view upon its closure.
     */
    public void editDoctor() {
        DoctorEditDialog dialog = new DoctorEditDialog(mainFrame, true, masterDoctorList);
        dialog.setVisible(true);
        // After the dialog closes, the master list may have been updated.
        // Refresh the table to show any potential changes.
        view.populateDoctorTable(masterDoctorList);
    }

    /**
     * Filters the doctor list based on user input from the view and updates the
     * table.
     */
    public void filterDoctorList() {
        String selectedCriterion = view.getFilterCriterion();
        String searchText = view.getFilterSearchText().trim().toLowerCase();

        // If search bar is empty, show the full list
        if (searchText.isEmpty()) {
            view.populateDoctorTable(masterDoctorList);
            return;
        }

        // Perform a linear search on the master list
        DoublyLinkedList<Pair<String, Doctor>> searchResults = new DoublyLinkedList<>();
        for (Pair<String, Doctor> pair : masterDoctorList) {
            Doctor doctor = pair.getValue();
            String doctorId = pair.getKey();
            boolean match = false;

            // Check against the selected filter criterion
            switch (selectedCriterion) {
                case "ID":
                    if (doctorId.toLowerCase().contains(searchText)) {
                        match = true;
                    }
                    break;
                case "Name":
                    if (doctor.getName().toLowerCase().contains(searchText)) {
                        match = true;
                    }
                    break;
                case "Position":
                    if (doctor.getPosition().toLowerCase().contains(searchText)) {
                        match = true;
                    }
                    break;
            }

            if (match) {
                searchResults.insertLast(pair);
            }
        }
        // Update the table to show only the search results
        view.populateDoctorTable(searchResults);
    }

    /**
     * Sorts the master doctor list based on the user's selection and updates
     * the view.
     */
    public void sortDoctorList() {
        String selectedSort = view.getSelectedSort();
        if (masterDoctorList == null) {
            return;
        }

        // Always sort in ascending order first
        masterDoctorList.sort();
        // If "DESC" is selected, reverse the sorted list
        if ("DESC".equals(selectedSort)) {
            masterDoctorList.reverse();
        }
        // Update the table with the sorted list
        view.populateDoctorTable(masterDoctorList);
    }

    /**
     * Triggers the generation of specialization and availability reports.
     */
    public void generateReports() {
        ReportGenerator.generateSpecializationReport(masterDoctorList);
        ReportGenerator.generateAvailabilityReport(masterDoctorList);
        JOptionPane.showMessageDialog(view, "Reports generated successfully!");
    }

    /**
     * Saves changes and navigates back to the previous panel.
     */
    public void Exit() {
        mainFrame.showPanel("doctorManagement");
    }
}
