package enitity;

import java.io.Serializable;

/**
 *
 * @author Chok Chun Fai
 */
public class Doctor implements Serializable {

    private final String ID_PREFIX = "D";

    private String name;
    private int age;
    private String phoneNumber;
    private String position;
    private String doctorID;
    private String status;
    private static int doctorIndex = 0;

    public Doctor() {
        doctorIndex++;
        this.doctorID = setDoctorID(doctorIndex);
    }

    public Doctor(String name, int age, String phoneNumber, String position, String status) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.status = status;
        doctorIndex++;
        this.doctorID = setDoctorID(doctorIndex);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    private String setDoctorID(int doctorIndex) {

        String generateID = "";

        switch (Integer.toString(doctorIndex).length()) {
            case 1 ->
                generateID = ID_PREFIX.concat("00" + Integer.toString(doctorIndex));
            case 2 ->
                generateID = ID_PREFIX.concat("0" + Integer.toString(doctorIndex));
            case 3 ->
                generateID = ID_PREFIX.concat(Integer.toString(doctorIndex));
            default -> {
                System.out.println("Failed to generate ID");
            }
        }

        return generateID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getDoctorIndex() {
        return doctorIndex;
    }

    public static void setDoctorIndex(int doctorIndex) {
        Doctor.doctorIndex = doctorIndex;
    }

    public static void resetDoctorIndex() {
        Doctor.doctorIndex = 0;
    }

    @Override
    public String toString() {
        return "Doctor{name=" + name + ", age=" + age + ", phoneNumber=" + phoneNumber + ", position=" + position + ", doctorID=" + doctorID + ", status=" + status + '}';
    }

}
