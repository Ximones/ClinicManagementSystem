/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control.DoctorManagementController;

import adt.DoublyLinkedList;
import adt.Pair;
import boundary.DoctorManagementUI.DoctorSchedulePanel;
import enitity.Doctor;
import enitity.DutySlot;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import javax.swing.JOptionPane;
import utility.FileUtils;
import utility.ReportGenerator;

/**
 * @author Chok Chun Fai
 * Control class for DoctorSchedulePanel. Handles all business
 * logic for managing doctor schedules.
 */
public class DoctorScheduleControl {

    private final DoctorSchedulePanel view;
    private DoublyLinkedList<DutySlot> thisWeekSchedule;
    private DoublyLinkedList<DutySlot> nextWeekSchedule;
    private DoublyLinkedList<Pair<String, Doctor>> masterDoctorList;

    // Constants can be managed by the controller
    public final DoublyLinkedList<String> DAYS = new DoublyLinkedList<>();
    public final DoublyLinkedList<String> SHIFTS = new DoublyLinkedList<>();

    public DoctorScheduleControl(DoctorSchedulePanel view) {
        this.view = view;

        // Initialize constants
        DAYS.insertLast("Monday");
        DAYS.insertLast("Tuesday");
        DAYS.insertLast("Wednesday");
        DAYS.insertLast("Thursday");
        DAYS.insertLast("Friday");
        DAYS.insertLast("Saturday");

        SHIFTS.insertLast("Morning (8am - 2pm)");
        SHIFTS.insertLast("Evening (2pm - 8pm)");

        runScheduleRolloverCheck(LocalDate.now());
        loadInitialData();
    }

    // Getters for the view to access data
    public DoublyLinkedList<DutySlot> getThisWeekSchedule() {
        return thisWeekSchedule;
    }

    public DoublyLinkedList<DutySlot> getNextWeekSchedule() {
        return nextWeekSchedule;
    }

    public DoublyLinkedList<Pair<String, Doctor>> getMasterDoctorList() {
        return masterDoctorList;
    }

    /**
     * Loads all required data from files.
     */
    private void loadInitialData() {
        masterDoctorList = (DoublyLinkedList<Pair<String, Doctor>>) FileUtils.readDataFromFile("doctors");
        if (masterDoctorList == null) {
            masterDoctorList = new DoublyLinkedList<>();
        }

        thisWeekSchedule = (DoublyLinkedList<DutySlot>) FileUtils.readDataFromFile("this_week_schedule");
        if (thisWeekSchedule == null) {
            thisWeekSchedule = new DoublyLinkedList<>();
        }

        nextWeekSchedule = (DoublyLinkedList<DutySlot>) FileUtils.readDataFromFile("next_week_schedule");
        if (nextWeekSchedule == null || nextWeekSchedule.isEmpty()) {
            initializeNextWeekSchedule();
        }
    }

    /**
     * Creates a new, empty schedule for the upcoming week.
     */
    private void initializeNextWeekSchedule() {
        nextWeekSchedule = new DoublyLinkedList<>();
        for (String day : DAYS) {
            for (String shift : SHIFTS) {
                nextWeekSchedule.insertLast(new DutySlot(day, shift));
            }
        }
    }

    /**
     * Saves the next week's schedule to file.
     */
    public void saveSchedule() {
        FileUtils.writeDataToFile("next_week_schedule", nextWeekSchedule);

        LocalDate today = LocalDate.now();
        LocalDate nextWeekMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        DoublyLinkedList<String> dateList = new DoublyLinkedList<>();
        dateList.insertLast(nextWeekMonday.toString());
        FileUtils.writeDataToFile("schedule_info", dateList);

        JOptionPane.showMessageDialog(view, "Next week's schedule has been saved successfully!");
    }

    /**
     * Handles the logic of assigning a doctor to a duty slot.
     *
     * @param day The day of the week.
     * @param shift The shift (Morning/Evening).
     * @param doctorName The name of the doctor to assign.
     */
    public void assignDoctorToSlot(String day, String shift, String doctorName) {
        DutySlot targetSlot = findDutySlot(nextWeekSchedule, day, shift);
        Doctor doctorToAssign = findDoctorByName(doctorName);

        if (targetSlot != null) {
            targetSlot.setAssignedDoctor(doctorToAssign);
            System.out.println("SUCCESS: Assigned " + (doctorToAssign != null ? doctorToAssign.getName() : "Unassigned") + " to " + day + " " + shift);
        } else {
            System.out.println("ERROR: Could not find DutySlot for Day: [" + day + "], Shift: [" + shift + "]");
        }
    }

    /**
     * Generates a PDF report for the next week's schedule.
     */
    public void generateScheduleReport() {
        ReportGenerator.generateWeeklyScheduleReport(nextWeekSchedule, "Next Week's Duty Schedule", DAYS, SHIFTS);
        JOptionPane.showMessageDialog(view, "Next Week's Schedule has been generated!");
    }

    /**
     * Checks if the week has changed and rolls over schedules if necessary.
     */
    private void runScheduleRolloverCheck(LocalDate today) {
        DoublyLinkedList<String> dateList = (DoublyLinkedList<String>) FileUtils.readDataFromFile("schedule_info");
        if (dateList == null || dateList.isEmpty()) {
            return;
        }

        LocalDate scheduleStartDate = LocalDate.parse(dateList.getFirst().getEntry());
        if (!today.isBefore(scheduleStartDate)) {
            System.out.println("New week detected! Rolling over schedules...");
            File thisWeekFile = new File("dao/this_week_schedule.bin");
            File nextWeekFile = new File("dao/next_week_schedule.bin");

            if (nextWeekFile.exists()) {
                if (thisWeekFile.exists()) {
                    thisWeekFile.delete();
                }
                if (nextWeekFile.renameTo(thisWeekFile)) {
                    LocalDate newScheduleStartDate = scheduleStartDate.plusWeeks(1);
                    DoublyLinkedList<String> newDateList = new DoublyLinkedList<>();
                    newDateList.insertLast(newScheduleStartDate.toString());
                    FileUtils.writeDataToFile("schedule_info", newDateList);
                }
            }
        }
    }

    /**
     * Simulates a rollover for testing purposes.
     */
    public void testRollover() {
        LocalDate futureDate = LocalDate.now().plusWeeks(1);
        System.out.println("--- TESTING ROLLOVER WITH FAKE DATE: " + futureDate + " ---");
        runScheduleRolloverCheck(futureDate);

        // Reload data and refresh the view
        loadInitialData();
        view.refreshTables();
        JOptionPane.showMessageDialog(view, "Rollover test complete and tables refreshed!");
    }

    // Helper methods that were in the view
    private Doctor findDoctorByName(String name) {
        if (name == null || name.equals("Unassigned")) {
            return null;
        }
        for (Pair<String, Doctor> pair : masterDoctorList) {
            Doctor doctor = pair.getValue();
            if (doctor.getName().equals(name)) {
                return doctor;
            }
        }
        return null;
    }

    private DutySlot findDutySlot(DoublyLinkedList<DutySlot> schedule, String day, String shift) {
        for (DutySlot slot : schedule) {
            if (slot.getDayOfWeek().equals(day) && slot.getShift().equals(shift)) {
                return slot;
            }
        }
        return null;
    }
}
