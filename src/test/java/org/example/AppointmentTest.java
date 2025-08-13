package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // Initialize a default appointment before each test
        appointment = new Appointment("A001", "P001", "D001", "2024-07-30", "10:00 AM", "General Checkup");
    }

    @Test
    void testAppointmentInitializationAndGetters() {
        assertNotNull(appointment, "Appointment object should not be null after initialization");
        assertEquals("A001", appointment.getAppointmentId(), "Appointment ID should match the initialized value");
        assertEquals("P001", appointment.getPatientId(), "Patient ID should match the initialized value");
        assertEquals("D001", appointment.getDoctorId(), "Doctor ID should match the initialized value");
        assertEquals("2024-07-30", appointment.getDate(), "Date should match the initialized value");
        assertEquals("10:00 AM", appointment.getTime(), "Time should match the initialized value");
        assertEquals("General Checkup", appointment.getDescription(), "Description should match the initialized value");
        assertFalse(appointment.isCompleted(), "Appointment should be incomplete by default");
    }

    @Test
    void testSetCompleted() {
        appointment.setCompleted(true);
        assertTrue(appointment.isCompleted(), "Appointment should be marked as completed");

        appointment.setCompleted(false);
        assertFalse(appointment.isCompleted(), "Appointment should be marked as incomplete");
    }

    @Test
    void testAppointmentWithNullStringValues() {
        Appointment nullAppointment = new Appointment("A002", null, null, null, null, null);
        assertNotNull(nullAppointment, "Appointment object should be created even with null strings");
        assertEquals("A002", nullAppointment.getAppointmentId());
        assertNull(nullAppointment.getPatientId(), "Patient ID should be null");
        assertNull(nullAppointment.getDoctorId(), "Doctor ID should be null");
        assertNull(nullAppointment.getDate(), "Date should be null");
        assertNull(nullAppointment.getTime(), "Time should be null");
        assertNull(nullAppointment.getDescription(), "Description should be null");
        assertFalse(nullAppointment.isCompleted());
    }

    @Test
    void testAppointmentWithEmptyStringValues() {
        Appointment emptyAppointment = new Appointment("A003", "", "", "", "", "");
        assertNotNull(emptyAppointment, "Appointment object should be created even with empty strings");
        assertEquals("A003", emptyAppointment.getAppointmentId());
        assertTrue(emptyAppointment.getPatientId().isEmpty(), "Patient ID should be empty");
        assertTrue(emptyAppointment.getDoctorId().isEmpty(), "Doctor ID should be empty");
        assertTrue(emptyAppointment.getDate().isEmpty(), "Date should be empty");
        assertTrue(emptyAppointment.getTime().isEmpty(), "Time should be empty");
        assertTrue(emptyAppointment.getDescription().isEmpty(), "Description should be empty");
        assertFalse(emptyAppointment.isCompleted());
    }

    @ParameterizedTest
    @CsvSource({
            "A004, P002, D002, 2024-08-01, 11:30 AM, Follow-up",
            "A005, P003, D003, 2024-08-02, 09:00 AM, Emergency",
            "A006, P004, D001, 2024-08-03, 02:15 PM, Vaccination"
    })
    void testAppointmentInitializationWithCsvSource(String appointmentId, String patientId, String doctorId,
                                                    String date, String time, String description) {
        Appointment app = new Appointment(appointmentId, patientId, doctorId, date, time, description);

        assertNotNull(app);
        assertEquals(appointmentId, app.getAppointmentId());
        assertEquals(patientId, app.getPatientId());
        assertEquals(doctorId, app.getDoctorId());
        assertEquals(date, app.getDate());
        assertEquals(time, app.getTime());
        assertEquals(description, app.getDescription());
        assertFalse(app.isCompleted()); // Should always be incomplete(false) on creation
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSetCompletedParameterized(boolean completedStatus) {
        appointment.setCompleted(completedStatus);
        assertEquals(completedStatus, appointment.isCompleted(), "Appointment completed status should match parameterized value");
    }

    @Test
    void testAppointmentConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> new Appointment("A007", "P005", "D004", "2024-08-04", "04:00 PM", "Annual Checkup"),
                "Appointment constructor should not throw an exception for valid inputs");
    }
}