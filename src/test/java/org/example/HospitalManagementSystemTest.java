package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HospitalManagementSystemTest {

    private HospitalManagementSystem hms;

    @BeforeEach
    void setUp() {
        hms = new HospitalManagementSystem();
    }

    @Test
    void testAuthenticateValidUser() {
        User user = hms.authenticateUser("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testAuthenticateInvalidUser() {
        User user = hms.authenticateUser("unknown", "wrong");
        assertNull(user);
    }

    @Test
    void testGetAllDoctors() {
        List<Doctor> doctors = hms.getAllDoctors();
        assertEquals(3, doctors.size());
        assertEquals("Dr. Smith", doctors.get(0).getName());
    }

    @Test
    void testAddAndDeletePatient() {
        Patient newPatient = new Patient("PAT99", "Test Patient", 40, "Female", "Somewhere", "999-9999");
        hms.addPatient(newPatient);

        assertNotNull(hms.getPatientById("PAT99"));

        hms.deletePatient("PAT99");
        assertNull(hms.getPatientById("PAT99"));
    }

    @Test
    void testMarkAppointmentCompleted() {
        hms.markAppointmentCompleted("APP1");
        Appointment appt = hms.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().equals("APP1")).findFirst().orElse(null);

        assertNotNull(appt);
        assertTrue(appt.isCompleted());
    }

    @Test
    void testMarkBillPaid() {
        hms.markBillPaid("BILL1");
        Bill bill = hms.getAllBills().stream()
                .filter(b -> b.getBillId().equals("BILL1")).findFirst().orElse(null);

        assertNotNull(bill);
        assertTrue(bill.isPaid());
    }

    @Test
    void testUpdateUserPassword() {
        hms.updateUserPassword("admin", "newpass123");
        User updated = hms.authenticateUser("admin", "newpass123");

        assertNotNull(updated);
        assertEquals("newpass123", updated.getPassword());
    }

    @Test
    void testDeleteUserFailsIfOnlyOne() {
        hms.deleteUser("doctor1");
        hms.deleteUser("reception1");

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            hms.deleteUser("admin");
        });

        assertEquals("Cannot delete last user", exception.getMessage());
    }
}
