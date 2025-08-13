package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class MedicalRecord implements Serializable {
    private String recordId;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String visitDate;
    private String visitTime;

    public MedicalRecord(String recordId, String patientId, String doctorId,
                         String diagnosis, String treatment, String notes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;

        // Set current date and time
        LocalDateTime now = LocalDateTime.now();
        this.visitDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.visitTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Getters
    public String getRecordId() { return recordId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public String getNotes() { return notes; }
    public String getVisitDate() { return visitDate; }
    public String getVisitTime() { return visitTime; }

    @Override
    public String toString() {
        return visitDate + " - " + diagnosis;
    }
}
