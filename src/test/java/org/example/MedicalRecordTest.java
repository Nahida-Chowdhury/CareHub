package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

class MedicalRecordTest {

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord("MR001", "P001", "DOC1", "Hypertension", "Prescribed Lisinopril 10mg daily", "Patient shows good response to treatment");
    }

    @ParameterizedTest
    @CsvSource({
            "MR001, P001, DOC1, Hypertension, Prescribed Lisinopril 10mg daily, Patient shows good response to treatment",
            "MR002, P002, DOC2, Diabetes, Insulin therapy, Regular monitoring required",
            "MR003, P003, DOC3, Asthma, Inhaler prescribed, Avoid allergens"
    })
    @DisplayName("Should create medical records with various valid inputs")
    void testMedicalRecordCreationWithMultipleInputs(String recordId, String patientId, String doctorId,
                                                     String diagnosis, String treatment, String notes) {
        MedicalRecord record = new MedicalRecord(recordId, patientId, doctorId, diagnosis, treatment, notes);

        assertNotNull(record);
        assertEquals(recordId, record.getRecordId());
        assertEquals(patientId, record.getPatientId());
        assertEquals(doctorId, record.getDoctorId());
        assertEquals(diagnosis, record.getDiagnosis());
        assertEquals(treatment, record.getTreatment());
        assertEquals(notes, record.getNotes());
        assertNotNull(record.getVisitDate());
        assertNotNull(record.getVisitTime());
    }

    @ParameterizedTest
    @MethodSource("provideMedicalRecordData")
    @DisplayName("Should return correct values from getter methods")
    void testGetterMethods(MedicalRecordTestData testData) {
        MedicalRecord record = new MedicalRecord(testData.recordId, testData.patientId, testData.doctorId,
                testData.diagnosis, testData.treatment, testData.notes);

        assertEquals(testData.recordId, record.getRecordId());
        assertEquals(testData.patientId, record.getPatientId());
        assertEquals(testData.doctorId, record.getDoctorId());
        assertEquals(testData.diagnosis, record.getDiagnosis());
        assertEquals(testData.treatment, record.getTreatment());
        assertEquals(testData.notes, record.getNotes());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should handle null and empty strings")
    void testNullAndEmptyStrings(String input) {
        MedicalRecord record = new MedicalRecord(input, input, input, input, input, input);

        assertEquals(input, record.getRecordId());
        assertEquals(input, record.getPatientId());
        assertNotNull(record.getVisitDate());
        assertNotNull(record.getVisitTime());
    }

    @Test
    @DisplayName("Should set correct date and time format")
    void testDateTimeFormat() {
        MedicalRecord record = new MedicalRecord("MR001", "P001", "DOC1", "Test Diagnosis", "Test Treatment", "Test Notes");

        String visitDate = record.getVisitDate();
        String visitTime = record.getVisitTime();

        assertNotNull(visitDate);
        assertNotNull(visitTime);
        assertTrue(visitDate.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(visitTime.matches("\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("Should be serializable")
    void testSerializable() {
        assertTrue(medicalRecord instanceof java.io.Serializable);
    }

    static Stream<MedicalRecordTestData> provideMedicalRecordData() {
        return Stream.of(
                new MedicalRecordTestData("MR001", "P001", "DOC1", "Hypertension", "Lisinopril", "Good response"),
                new MedicalRecordTestData("MR002", "P002", "DOC2", "Diabetes", "Insulin", "Monitor blood sugar")
        );
    }

    static class MedicalRecordTestData {
        final String recordId;
        final String patientId;
        final String doctorId;
        final String diagnosis;
        final String treatment;
        final String notes;

        MedicalRecordTestData(String recordId, String patientId, String doctorId,
                              String diagnosis, String treatment, String notes) {
            this.recordId = recordId;
            this.patientId = patientId;
            this.doctorId = doctorId;
            this.diagnosis = diagnosis;
            this.treatment = treatment;
            this.notes = notes;
        }
    }
}
