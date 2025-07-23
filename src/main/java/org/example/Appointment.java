package org.example;

import java.io.Serializable;

class Appointment implements Serializable {
    private String appointmentId, patientId, doctorId, date, time, description;
    private boolean completed;

    public Appointment(String appointmentId, String patientId, String doctorId,
                       String date, String time, String description) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // Getters and setters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
