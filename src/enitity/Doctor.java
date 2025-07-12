package enitity;

import java.util.Objects;

/**
 *
 * @author Chok Chun Fai
 */
public class Doctor {

    private String name;
    private int age;
    private String phoneNumber;
    private String position;

    public Doctor(String name, int age, String phoneNumber, String position) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.position = position;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + this.age;
        hash = 23 * hash + Objects.hashCode(this.phoneNumber);
        hash = 23 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Doctor other = (Doctor) obj;
        if (this.age != other.age) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.phoneNumber, other.phoneNumber)) {
            return false;
        }
        return Objects.equals(this.position, other.position);
    }

    @Override
    public String toString() {
        return "Doctor{" + "name=" + name + ", age=" + age + ", phoneNumber=" + phoneNumber + ", position=" + position + '}';
    }

}
