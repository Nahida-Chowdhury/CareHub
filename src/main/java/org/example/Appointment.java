package org.example;

import java.io.Serializable;

class Appointment implements Serializable {
    private String appointmentId, patientId, doctorId, date, time, description;
    private String patientName, doctorName; // Added for better display
    private boolean completed;

    // Original constructor
    public Appointment(String appointmentId, String patientId, String doctorId,
                       String date, String time, String description) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.description = description;
        this.completed = false;
    }

    // Enhanced constructor with names
    public Appointment(String appointmentId, String patientId, String doctorId,
                       String date, String time, String description,
                       String patientName, String doctorName) {
        this(appointmentId, patientId, doctorId, date, time, description);
        this.patientName = patientName;
        this.doctorName = doctorName;
    }

    // Utility method to set names after creation
    public void setNames(String patientName, String doctorName) {
        this.patientName = patientName;
        this.doctorName = doctorName;
    }

    // Utility method to populate names from Patient and Doctor objects
    public void setNames(Patient patient, Doctor doctor) {
        this.patientName = patient != null ? patient.getName() : null;
        this.doctorName = doctor != null ? doctor.getName() : null;
    }

    // Getters and setters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    // Enhanced toString for better debugging
    @Override
    public String toString() {
        return String.format("Appointment[%s: %s with %s on %s at %s]",
                appointmentId,
                patientName != null ? patientName : "Patient(" + patientId + ")",
                doctorName != null ? doctorName : "Doctor(" + doctorId + ")",
                date, time);
    }

    // Method for display in UI components
    public String getDisplayString() {
        return String.format("%s - %s with %s (%s %s)",
                appointmentId,
                patientName != null ? patientName : "Unknown Patient",
                doctorName != null ? doctorName : "Unknown Doctor",
                date, time);
    }
}
