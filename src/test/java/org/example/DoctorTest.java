package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    @Test
    void testDoctorInitializationAndGetters() {
        Doctor doctor = new Doctor("D001", "Dr. Smith", "Cardiology", "Mon-Fri");

        assertEquals("D001", doctor.getDoctorId());
        assertEquals("Dr. Smith", doctor.getName());
        assertEquals("Cardiology", doctor.getSpecialization());
        assertEquals("Mon-Fri", doctor.getAvailability());
        assertNotNull(doctor);  // Ensures the object is created
    }

    @Test
    void testToString() {
        Doctor doctor = new Doctor("D002", "Dr. Jane", "Neurology", "Tue-Thu");
        String expected = "Dr. Jane (Neurology)";
        assertEquals(expected, doctor.toString());
    }

    @Test
    void testNullValues() {
        Doctor doctor = new Doctor(null, null, null, null);

        assertNull(doctor.getDoctorId());
        assertNull(doctor.getName());
        assertNull(doctor.getSpecialization());
        assertNull(doctor.getAvailability());
    }

    @Test
    void testObjectReference() {
        Doctor doctor1 = new Doctor("D003", "Dr. Allen", "ENT", "Wed");
        Doctor doctor2 = doctor1;
        Doctor doctor3 = new Doctor("D003", "Dr. Allen", "ENT", "Wed");

        assertSame(doctor1, doctor2);        // Same reference
        assertNotSame(doctor1, doctor3);     // Different object instances
    }

    @Test
    void testDoesNotThrowWhenCreatingDoctor() {
        assertDoesNotThrow(() -> new Doctor("D004", "Dr. Eva", "Pediatrics", "Fri-Sat"));
    }
}
