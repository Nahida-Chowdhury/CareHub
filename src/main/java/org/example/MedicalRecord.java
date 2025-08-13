package org.example;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class MedicalRecord implements Serializable {
    @BsonProperty("record_id")
    private String recordId;

    @BsonProperty("patient_id")
    private String patientId;

    @BsonProperty("doctor_id")
    private String doctorId;

    private String diagnosis;
    private String treatment;
    private String notes;

    @BsonProperty("visit_date")
    private String visitDate;

    @BsonProperty("visit_time")
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
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }

    @Override
    public String toString() {
        return visitDate + " - " + diagnosis;
    }
}
