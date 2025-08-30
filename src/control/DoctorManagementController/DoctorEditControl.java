package control.DoctorManagementController;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.DoctorManagementUI.DoctorEditDialog;
import enitity.Doctor;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import utility.FileUtils;

/**
 * Control class for DoctorEditDialog. Handles logic for searching and updating
 * doctor information.
 */
public class DoctorEditControl {
    
    private final DoctorEditDialog view;
    private final DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;
    private Pair<String, Doctor> doctorPairToEdit = null;
    
    public DoctorEditControl(DoctorEditDialog view, DoublyLinkedList<Pair<String, Doctor>> masterDoctorList) {
        this.view = view;
        this.masterDoctorList = masterDoctorList;
    }

    /**
     * Searches for a doctor by ID.
     *
     * @param doctorId The ID to search for.
     */
    public void searchDoctorById(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a Doctor ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        doctorPairToEdit = null; // Reset previous search
        for (Pair<String, Doctor> pair : masterDoctorList) {
            if (pair.getKey().equalsIgnoreCase(doctorId.trim())) {
                doctorPairToEdit = pair;
                break;
            }
        }
        
        if (doctorPairToEdit != null) {
            view.displayDoctorInfo(doctorPairToEdit.getKey(), doctorPairToEdit.getValue());
            view.setFieldsEditable(true);
        } else {
            JOptionPane.showMessageDialog(view, "Doctor ID not found.", "Search Failed", JOptionPane.ERROR_MESSAGE);
            view.setFieldsEditable(false);
            view.clearForm();
        }
    }

    /**
     * Saves the updated doctor information.
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

            // Update the entity object
            Doctor doctor = doctorPairToEdit.getValue();
            doctor.setName(name);
            doctor.setAge(age);
            doctor.setPhoneNumber(phone);
            doctor.setPosition(position);
            doctor.setStatus(status);

            JOptionPane.showMessageDialog(view, "Doctor information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            FileUtils.writeDataToFile("doctors", masterDoctorList);
            view.dispose(); // Close the dialog

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Please enter a valid number for Age.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
