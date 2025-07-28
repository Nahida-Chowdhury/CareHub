package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IDGeneratorTest {

    private HospitalManagementSystem system;

    @BeforeEach
    void setUp() {
        system = new HospitalManagementSystem();
        IDGenerator.initializeCounters(system);
    }

    @Test
    void testGeneratePatientID() {
        String patientID = IDGenerator.generatePatientID();
        assertEquals("PAT4", patientID);
    }

    @Test
    void testGenerateDoctorID() {
        String doctorID = IDGenerator.generateDoctorID();
        assertEquals("DOC4", doctorID);
    }

    @Test
    void testGenerateAppointmentID() {
        String appointmentID = IDGenerator.generateAppointmentID();
        assertEquals("APP3", appointmentID);
    }

    @Test
    void testGenerateBillID() {
        String billID = IDGenerator.generateBillID();
        assertEquals("BILL3", billID);
    }

    @Test
    void testMultipleGenerations() {
        assertEquals("PAT4", IDGenerator.generatePatientID());
        assertEquals("PAT5", IDGenerator.generatePatientID());

        assertEquals("DOC4", IDGenerator.generateDoctorID());
        assertEquals("DOC5", IDGenerator.generateDoctorID());
    }
}
