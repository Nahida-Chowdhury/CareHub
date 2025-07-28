package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient("P001", "John Doe", 30, "Male", "123 Main St", "555-1234");
    }

    @Test
    void testPatientInitializationAndGetters() {
        assertNotNull(patient, "Patient object should not be null after initialization");
        assertEquals("P001", patient.getPatientId(), "Patient ID should match the initialized value");
        assertEquals("John Doe", patient.getName(), "Patient name should match the initialized value");
        assertEquals(30, patient.getAge(), "Patient age should match the initialized value");
        assertEquals("Male", patient.getGender(), "Patient gender should match the initialized value");
        assertEquals("123 Main St", patient.getAddress(), "Patient address should match the initialized value");
        assertEquals("555-1234", patient.getPhone(), "Patient phone should match the initialized value");
    }

    @Test
    void testToStringMethod() {
        String expectedToString = "John Doe (P001)";
        assertEquals(expectedToString, patient.toString(), "toString() should return patient name and ID in the specified format");
    }

    @Test
    void testPatientWithNullStringValues() {
        Patient nullPatient = new Patient("P002", null, 25, null, null, null);
        assertNotNull(nullPatient, "Patient object should be created even with null strings");
        assertEquals("P002", nullPatient.getPatientId());
        assertNull(nullPatient.getName(), "Name should be null");
        assertEquals(25, nullPatient.getAge());
        assertNull(nullPatient.getGender(), "Gender should be null");
        assertNull(nullPatient.getAddress(), "Address should be null");
        assertNull(nullPatient.getPhone(), "Phone should be null");
    }

    @Test
    void testPatientWithEmptyStringValues() {
        Patient emptyPatient = new Patient("P003", "", 45, "", "", "");
        assertNotNull(emptyPatient, "Patient object should be created even with empty strings");
        assertEquals("P003", emptyPatient.getPatientId());
        assertTrue(emptyPatient.getName().isEmpty(), "Name should be empty");
        assertEquals(45, emptyPatient.getAge());
        assertTrue(emptyPatient.getGender().isEmpty(), "Gender should be empty");
        assertTrue(emptyPatient.getAddress().isEmpty(), "Address should be empty");
        assertTrue(emptyPatient.getPhone().isEmpty(), "Phone should be empty");
    }

    @ParameterizedTest
    @CsvSource({
            "P004, Alice Smith, 28, Female, 456 Oak Ave, 555-5678",
            "P005, Bob Johnson, 55, Male, 789 Pine St, 555-9012",
            "P006, Jane Doe, 0, Female, 321 Elm St, 555-3456"
    })
    void testPatientInitializationWithCsvSource(String id, String name, int age, String gender, String address, String phone) {
        Patient p = new Patient(id, name, age, gender, address, phone);

        assertNotNull(p);
        assertEquals(id, p.getPatientId());
        assertEquals(name, p.getName());
        assertEquals(age, p.getAge());
        assertEquals(gender, p.getGender());
        assertEquals(address, p.getAddress());
        assertEquals(phone, p.getPhone());
    }

    @Test
    void testPatientConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> new Patient("P007", "Grace Lee", 40, "Female", "101 Cedar Ln", "555-7890"),
                "Patient constructor should not throw an exception for valid inputs");
    }
}