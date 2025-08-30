package control.DoctorManagementController;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.DoctorManagementUI.DoctorEditDialog;
import enitity.Doctor;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import utility.FileUtils;

/**
 *
 * @author Chok Chun Fai
 */
/**
 * Control class for DoctorEditDialog. Handles logic for searching and updating
 * doctor information.
 */
public class DoctorEditControl {

    private final DoctorEditDialog view; // The dialog window this controller manages
    private final DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;  // The master list of all doctors
    private Pair<String, Doctor> doctorPairToEdit = null; // Stores the doctor currently being edited

    /**
     * Constructor for DoctorEditControl.
     *
     * @param view The DoctorEditDialog instance.
     * @param masterDoctorList The complete list of doctors.
     */
    public DoctorEditControl(DoctorEditDialog view, DoublyLinkedList<Pair<String, Doctor>> masterDoctorList) {
        this.view = view;
        this.masterDoctorList = masterDoctorList;
    }

    /**
     * Searches for a doctor by ID using a binary search on the master list.
     * Updates the view to display the doctor's info or an error message.
     *
     * @param doctorId The ID to search for.
     */
    public void searchDoctorById(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a Doctor ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // The master list MUST be sorted by ID for binary search to work.
        masterDoctorList.sort();

        //    Create a "dummy" Pair object with the key to search for.
        //    The Doctor value can be null because the Pair's compareTo method only uses the key.
        Pair<String, Doctor> keyToFind = new Pair<>(doctorId.trim().toUpperCase(), null);

        // Call the binarySearch method from DoublyLinkedList.
        doctorPairToEdit = masterDoctorList.binarySearch(keyToFind);

        if (doctorPairToEdit != null) {
            // If found, display the info and make fields editable
            view.displayDoctorInfo(doctorPairToEdit.getKey(), doctorPairToEdit.getValue());
            view.setFieldsEditable(true);
        } else {
            // If not found, show error and clear/disable the form
            JOptionPane.showMessageDialog(view, "Doctor ID not found.", "Search Failed", JOptionPane.ERROR_MESSAGE);
            view.setFieldsEditable(false);
            view.clearForm();
        }

    }

    /**
     * Validates and saves the updated doctor information back to the master
     * list and persists the changes to a file.
     */
    public void saveDoctorChanges() {
        if (doctorPairToEdit == null) {
            JOptionPane.showMessageDialog(view, "Please search for a doctor first.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Get updated info from the view
            String name = view.getDoctorName();
            int age = view.getDoctorAge();
            String phone = view.getDoctorPhone();
            String position = view.getDoctorPosition();
            String status = view.getDoctorStatus();

            // Basic validation
            if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Name and Phone cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (age < 23 || age > 80) {
                JOptionPane.showMessageDialog(view, "Please enter a realistic age for a doctor (23-80).", "Invalid Age", JOptionPane.ERROR_MESSAGE);
                return; // Stop the save process
            }

            String phoneRegex = "^(\\+?6?01)[0-9]{7,9}$";
            if (!Pattern.matches(phoneRegex, phone)) {
                JOptionPane.showMessageDialog(view, "Please enter a valid Malaysian mobile number (e.g., 0123456789).", "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the Doctor object (the entity)
            Doctor doctor = doctorPairToEdit.getValue();
            doctor.setName(name);
            doctor.setAge(age);
            doctor.setPhoneNumber(phone);
            doctor.setPosition(position);
            doctor.setStatus(status);

            // Inform the user and save the entire updated list to the file
            JOptionPane.showMessageDialog(view, "Doctor information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            FileUtils.writeDataToFile("doctors", masterDoctorList);
            view.dispose(); // Close the dialog

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Please enter a valid number for Age.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
