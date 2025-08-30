/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.DoctorManagementController;

import adt.Pair;
import boundary.DoctorManagementUI.DoctorAddDialog;
import enitity.Doctor;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Chok Chun Fai
 */
/**
 * Control class for DoctorAddDialog. Handles validation and creation of a new
 * Doctor entity.
 */
public class DoctorAddControl {

    private final DoctorAddDialog view; // The dialog window this controller manages
    private final Doctor newDoctor; // The entity being created
    private final int initialDoctorIndex;  // To store the index before creation for cancellation purposes

    /**
     * Constructor for the DoctorAddControl.
     *
     * @param view The DoctorAddDialog instance that this controller will
     * manage.
     */
    public DoctorAddControl(DoctorAddDialog view) {
        this.view = view;
        this.newDoctor = new Doctor(); // Create a new doctor instance to work with
        this.initialDoctorIndex = Doctor.getDoctorIndex(); // Store the index before creation

        // The view can be updated with the new ID immediately
        view.setDoctorId(newDoctor.getDoctorID());
    }

    /**
     * Validates form data from the view and populates the new Doctor object. If
     * successful, it sets the result on the view and closes it.
     */
    public void saveNewDoctor() {
        try {
            // 1. Get data from the view
            String name = view.getDoctorName().trim();
            String ageStr = view.getDoctorAge().trim();
            String phone = view.getDoctorPhone().trim();
            String position = view.getDoctorPosition();

            // 2. Perform validation
            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(view, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String phoneRegex = "^(\\+?6?01)[0-9]{7,9}$";
            if (!Pattern.matches(phoneRegex, phone)) {
                JOptionPane.showMessageDialog(view, "Please enter a valid Malaysian mobile number (e.g., 0123456789).", "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age = Integer.parseInt(ageStr);
            if (age < 23 || age > 80) {
                JOptionPane.showMessageDialog(view, "Please enter a realistic age for a doctor (23-80).", "Invalid Age", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Update the entity with validated data
            newDoctor.setName(name);
            newDoctor.setAge(age);
            newDoctor.setPhoneNumber(phone);
            newDoctor.setPosition(position);
            newDoctor.setStatus("Present"); // Default status for new doctors

            // 4. Set the result and close the view
            Pair<String, Doctor> result = new Pair<>(newDoctor.getDoctorID(), newDoctor);
            JOptionPane.showMessageDialog(view, "New doctor added successfully!");
            view.setResultAndClose(result);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Please enter a valid number for Age.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the cancellation of the add operation.
     * It resets the static doctor index to its original value.
     */
    public void cancelAdd() {
        // Reset the static doctor index to what it was before this dialog was opened
        Doctor.setDoctorIndex(initialDoctorIndex);
        view.setResultAndClose(null); // Set null result and close
    }
}
