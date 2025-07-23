package org.example;

import java.io.Serializable;

class Doctor implements Serializable {
    private String doctorId, name, specialization, availability;

    public Doctor(String doctorId, String name, String specialization, String availability) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.availability = availability;
    }

    // Getters
    public String getDoctorId() { return doctorId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getAvailability() { return availability; }

    @Override
    public String toString() { return name + " (" + specialization + ")"; }
}
