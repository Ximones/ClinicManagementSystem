/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 * @author deadb
 */
public class DoctorInformationControl {

    private DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;
    private final DoctorInformationPanel view; // A reference to the UI panel
    private final MainFrame mainFrame;

    public DoctorInformationControl(DoctorInformationPanel view, MainFrame mainFrame) {
        this.view = view;
        this.mainFrame = mainFrame;
        loadInitialData();
    }

    public DoublyLinkedList<Pair<String, Doctor>> getMasterDoctorList() {
        return masterDoctorList;
    }

    /**
     * Loads the initial doctor data from the file.
     */
    private void loadInitialData() {
        masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (masterDoctorList == null) {
            masterDoctorList = new DoublyLinkedList<>();
        }
        if (!masterDoctorList.isEmpty()) {
            Doctor.setDoctorIndex(masterDoctorList.getSize());
        }
    }

    /**
     * Handles the logic for adding a new doctor.
     */
    public void addNewDoctor() {
        DoctorAddDialog dialog = new DoctorAddDialog(mainFrame, true);
        dialog.setVisible(true);

        Pair<String, Doctor> newDoctorPair = dialog.getResult();
        if (newDoctorPair != null) {
            masterDoctorList.insertLast(newDoctorPair);
            FileUtils.writeDataToFile("doctors", masterDoctorList);
            view.populateDoctorTable(masterDoctorList); // Update the view
        }
    }

    /**
     * Handles the logic for editing an existing doctor.
     */
    public void editDoctor() {
        // You would enhance this to get the selected doctor from the table in the view
        DoctorEditDialog dialog = new DoctorEditDialog(mainFrame, true, masterDoctorList);
        dialog.setVisible(true);
        // After closing, refresh the table to show potential updates
        view.populateDoctorTable(masterDoctorList);
    }

    /**
     * Filters the doctor list based on user input from the view.
     */
    public void filterDoctorList() {
        String selectedCriterion = view.getFilterCriterion();
        String searchText = view.getFilterSearchText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            view.populateDoctorTable(masterDoctorList);
            return;
        }

        DoublyLinkedList<Pair<String, Doctor>> searchResults = new DoublyLinkedList<>();
        for (Pair<String, Doctor> pair : masterDoctorList) {
            Doctor doctor = pair.getValue();
            String doctorId = pair.getKey();
            boolean match = false;

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
        view.populateDoctorTable(searchResults);
    }

    /**
     * Sorts the master doctor list and updates the view.
     */
    public void sortDoctorList() {
        String selectedSort = view.getSelectedSort();
        if (masterDoctorList == null) {
            return;
        }

        // Always sort in ascending order first, then reverse if DESC is selected
        masterDoctorList.sort();
        if ("DESC".equals(selectedSort)) {
            masterDoctorList.reverse();
        }
        view.populateDoctorTable(masterDoctorList);
    }

    /**
     * Generates specialization and availability reports.
     */
    public void generateReports() {
        ReportGenerator.generateSpecializationReport(masterDoctorList);
        ReportGenerator.generateAvailabilityReport(masterDoctorList);
        JOptionPane.showMessageDialog(view, "Reports generated successfully!");
    }

    /**
     * Saves changes and navigates back.
     */
    public void Exit() {
        mainFrame.showPanel("doctorManagement");
    }
}
