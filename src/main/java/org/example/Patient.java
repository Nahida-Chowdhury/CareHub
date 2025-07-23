package org.example;

import java.io.Serializable;

class Patient implements Serializable {
    private String patientId, name, gender, address, phone;
    private int age;

    public Patient(String patientId, String name, int age, String gender, String address, String phone) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    @Override
    public String toString() { return name + " (" + patientId + ")"; }
}
