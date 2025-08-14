package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeAll
    static void setupAll() {
        System.out.println("Starting all tests for AppointmentTest class.");
    }

    @BeforeEach
    void setUp() {
        appointment = new Appointment("A001", "P001", "D001", "2024-07-30", "10:00 AM", "General Checkup");
        testPatient = new Patient("P001", "John Doe", 30, "Male", "123 Main St", "555-1234");
        testDoctor = new Doctor("D001", "Dr. Smith", "Cardiology", "Mon-Fri");
    }

    // Tests that the appointment is initialized correctly with all getters returning the expected values.
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

    // Tests the setter and getter for the 'completed' status of an appointment.
    @Test
    void testSetCompleted() {
        appointment.setCompleted(true);
        assertTrue(appointment.isCompleted(), "Appointment should be marked as completed");
        appointment.setCompleted(false);
        assertFalse(appointment.isCompleted(), "Appointment should be marked as incomplete");
    }

    // Tests appointment initialization with null string values to ensure no exceptions are thrown.
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

    // Tests appointment initialization with empty string values.
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

    // Tests that the appointment constructor does not throw an exception with valid input.
    @Test
    void testAppointmentConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> new Appointment("A007", "P005", "D004", "2024-08-04", "04:00 PM", "Annual Checkup"),
                "Appointment constructor should not throw an exception for valid inputs");
    }

    // Parametrized test for the new constructor that includes patient and doctor names.
    @ParameterizedTest
    @CsvSource({
            "A008, P008, D008, 2024-08-05, 03:00 PM, Follow-up, Jane Doe, Dr. Williams",
            "A009, P009, D009, 2024-08-06, 09:00 AM, Emergency, Robert Smith, Dr. Chen",
            "A010, P010, D010, 2024-08-07, 11:00 AM, X-Ray, Alice Johnson, Dr. Garcia"
    })
    void testEnhancedConstructorParameterized(String appointmentId, String patientId, String doctorId,
                                              String date, String time, String description,
                                              String patientName, String doctorName) {
        Appointment namedAppointment = new Appointment(appointmentId, patientId, doctorId, date, time, description, patientName, doctorName);
        assertNotNull(namedAppointment);
        assertEquals(patientId, namedAppointment.getPatientId());
        assertEquals(doctorId, namedAppointment.getDoctorId());
        assertEquals(patientName, namedAppointment.getPatientName());
        assertEquals(doctorName, namedAppointment.getDoctorName());
    }

    // Parametrized test for the getDisplayString() method with various name combinations.
    @ParameterizedTest
    @CsvSource({
            "John Doe, Dr. Smith, 'A001 - John Doe with Dr. Smith (2024-07-30 10:00 AM)'",
            ", Dr. Smith, 'A001 - Unknown Patient with Dr. Smith (2024-07-30 10:00 AM)'",
            "John Doe,, 'A001 - John Doe with Unknown Doctor (2024-07-30 10:00 AM)'",
            ",, 'A001 - Unknown Patient with Unknown Doctor (2024-07-30 10:00 AM)'"
    })
    void testGetDisplayStringParameterized(String patientName, String doctorName, String expectedDisplay) {
        appointment.setPatientName(patientName);
        appointment.setDoctorName(doctorName);
        assertEquals(expectedDisplay, appointment.getDisplayString());
    }

    // Parametrized test for the toString() method with different name combinations.
    @ParameterizedTest
    @CsvSource({
            "John Doe, Dr. Smith, 'Appointment[A001: John Doe with Dr. Smith on 2024-07-30 at 10:00 AM]'",
            ", Dr. Smith, 'Appointment[A001: Patient(P001) with Dr. Smith on 2024-07-30 at 10:00 AM]'",
            "John Doe,, 'Appointment[A001: John Doe with Doctor(D001) on 2024-07-30 at 10:00 AM]'",
            ",, 'Appointment[A001: Patient(P001) with Doctor(D001) on 2024-07-30 at 10:00 AM]'"
    })
    void testToStringParameterized(String patientName, String doctorName, String expectedToString) {
        appointment.setPatientName(patientName);
        appointment.setDoctorName(doctorName);
        assertEquals(expectedToString, appointment.toString());
    }

    // Parametrized test for the setCompleted method using boolean values.
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSetCompletedParameterized(boolean completedStatus) {
        appointment.setCompleted(completedStatus);
        assertEquals(completedStatus, appointment.isCompleted(), "Appointment completed status should match parameterized value");
    }

    // Tests the setNames method when provided with valid Patient and Doctor objects.
    @Test
    void testSetNamesWithPatientAndDoctorObjects() {
        assertNull(appointment.getPatientName());
        assertNull(appointment.getDoctorName());
        appointment.setNames(testPatient, testDoctor);
        assertEquals("John Doe", appointment.getPatientName());
        assertEquals("Dr. Smith", appointment.getDoctorName());
    }

    // Tests the setNames method when provided with null objects.
    @Test
    void testSetNamesWithPatientAndDoctorObjectsForNulls() {
        Patient nullPatient = null;
        Doctor nullDoctor = null;
        appointment.setNames(nullPatient, nullDoctor);
        assertNull(appointment.getPatientName());
        assertNull(appointment.getDoctorName());
    }

    @AfterEach
    void tearDown() {
        appointment = null;
        testPatient = null;
        testDoctor = null;
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("All tests for AppointmentTest class have finished.");
    }
}