/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enitity;

import java.io.Serializable;

/**
 *
 * @author deadb
 */
public class DutySlot implements Serializable {
    private String dayOfWeek; // e.g., "Monday"
    private String shift;     // e.g., "Morning (8am-4pm)"
    private Doctor assignedDoctor; // The doctor assigned to this slot

    public DutySlot(String dayOfWeek, String shift) {
        this.dayOfWeek = dayOfWeek;
        this.shift = shift;
        this.assignedDoctor = null;
    }

    public DutySlot(String dayOfWeek, String shift, Doctor assignedDoctor) {
        this.dayOfWeek = dayOfWeek;
        this.shift = shift;
        this.assignedDoctor = assignedDoctor;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setAssignedDoctor(Doctor assignedDoctor) {
        this.assignedDoctor = assignedDoctor;
    }

    @Override
    public String toString() {
        return "Schedule{" + "dayOfWeek=" + dayOfWeek + ", shift=" + shift + ", assignedDoctor=" + assignedDoctor + '}';
    }
    
    
}
