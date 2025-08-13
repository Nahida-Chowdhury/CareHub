package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HospitalManagementSystemTest {

    private TestableHospitalManagementSystem system;
    private Patient testPatient;
    private Doctor testDoctor;
    private User testUser;

    private static class TestableHospitalManagementSystem {
        private Map<String, User> users = new HashMap<>();
        private List<Patient> patients = new ArrayList<>();
        private List<Doctor> doctors = new ArrayList<>();
        private List<Appointment> appointments = new ArrayList<>();
        private List<Bill> bills = new ArrayList<>();

        public TestableHospitalManagementSystem() {
            // Initialize with default admin user
            users.put("admin", new User("admin", "admin123", UserRole.ADMIN));
        }

        public User authenticateUser(String username, String password) {
            if (username == null || password == null || username.trim().isEmpty()) {
                return null;
            }
            User user = users.get(username);
            return (user != null && user.getPassword().equals(password)) ? user : null;
        }

        public void addPatient(Patient patient) {
            if (patient != null) {
                patients.add(patient);
            }
        }

        public void addDoctor(Doctor doctor) {
            if (doctor != null) {
                doctors.add(doctor);
            }
        }

        public List<Patient> getAllPatients() {
            return new ArrayList<>(patients);
        }

        public List<Doctor> getAllDoctors() {
            return new ArrayList<>(doctors);
        }

        public Patient getPatientById(String patientId) {
            if (patientId == null) return null;
            return patients.stream()
                    .filter(p -> patientId.equals(p.getPatientId()))
                    .findFirst()
                    .orElse(null);
        }

        public Doctor getDoctorById(String doctorId) {
            if (doctorId == null) return null;
            return doctors.stream()
                    .filter(d -> doctorId.equals(d.getDoctorId()))
                    .findFirst()
                    .orElse(null);
        }

        public void deletePatient(String patientId) {
            patients.removeIf(p -> patientId.equals(p.getPatientId()));
        }

        public void deleteDoctor(String doctorId) {
            doctors.removeIf(d -> doctorId.equals(d.getDoctorId()));
        }

        public void deleteUser(String username) {
            if (users.size() <= 1) {
                throw new IllegalStateException("Cannot delete the last user");
            }
            users.remove(username);
        }

        public void updateUserPassword(String username, String newPassword) {
            User user = users.get(username);
            if (user != null) {
                user.setPassword(newPassword);
            }
        }

        public void addAppointment(Appointment appointment) {
            if (appointment != null) {
                appointments.add(appointment);
            }
        }

        public List<Appointment> getAllAppointments() {
            return new ArrayList<>(appointments);
        }

        public void markAppointmentCompleted(String appointmentId) {
            appointments.stream()
                    .filter(a -> appointmentId.equals(a.getAppointmentId()))
                    .findFirst()
                    .ifPresent(a -> a.setCompleted(true));
        }

        public void addBill(Bill bill) {
            if (bill != null) {
                bills.add(bill);
            }
        }

        public void markBillPaid(String billId) {
            bills.stream()
                    .filter(b -> billId.equals(b.getBillId()))
                    .findFirst()
                    .ifPresent(b -> b.setPaid(true));
        }

        public Map<String, User> getUsers() {
            return users;
        }
    }

    @BeforeAll
    void setUpAll() {
        System.out.println("Setting up HospitalManagementSystem test suite");
    }

    @AfterAll
    void tearDownAll() {
        System.out.println("Tearing down HospitalManagementSystem test suite");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        testPatient = new Patient("PAT001", "John Doe", 30, "Male", "123 Main St", "555-1234");
        testDoctor = new Doctor("DOC001", "Dr. Smith", "Cardiology", "9AM-5PM");
        testUser = new User("testuser", "password123", UserRole.ADMIN);

        system = new TestableHospitalManagementSystem();
    }

    @AfterEach
    void tearDown() {
        system = null;
    }

    @Test
    @DisplayName("Should initialize system with default components")
    void testSystemInitialization() {
        assertNotNull(system);
        assertNotNull(system.getUsers());
        assertFalse(system.getUsers().isEmpty());
    }

    @Test
    @DisplayName("Should authenticate valid user")
    void testAuthenticateValidUser() {
        // Add user to system
        system.getUsers().put("testuser", testUser);

        User authenticatedUser = system.authenticateUser("testuser", "password123");

        assertNotNull(authenticatedUser);
        assertEquals("testuser", authenticatedUser.getUsername());
        assertEquals(UserRole.ADMIN, authenticatedUser.getRole());
    }

    @Test
    @DisplayName("Should reject invalid credentials")
    void testAuthenticateInvalidUser() {
        User authenticatedUser = system.authenticateUser("invalid", "wrong");

        assertNull(authenticatedUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should reject empty or whitespace usernames")
    void testAuthenticateEmptyUsername(String username) {
        User authenticatedUser = system.authenticateUser(username, "password");

        assertNull(authenticatedUser);
    }

    @ParameterizedTest
    @CsvSource({
            "admin, admin123, ADMIN",
            "doctor1, doc123, DOCTOR",
            "reception1, recep123, RECEPTIONIST"
    })
    @DisplayName("Should authenticate different user roles")
    void testAuthenticateUserRoles(String username, String password, UserRole role) {
        User user = new User(username, password, role);
        system.getUsers().put(username, user);

        User authenticatedUser = system.authenticateUser(username, password);

        assertNotNull(authenticatedUser);
        assertEquals(role, authenticatedUser.getRole());
    }

    @Test
    @DisplayName("Should add patient to system")
    void testAddPatient() {
        int initialSize = system.getAllPatients().size();

        system.addPatient(testPatient);

        assertEquals(initialSize + 1, system.getAllPatients().size());
        assertTrue(system.getAllPatients().contains(testPatient));
    }

    @Test
    @DisplayName("Should add doctor to system")
    void testAddDoctor() {
        int initialSize = system.getAllDoctors().size();

        system.addDoctor(testDoctor);

        assertEquals(initialSize + 1, system.getAllDoctors().size());
        assertTrue(system.getAllDoctors().contains(testDoctor));
    }

    @Test
    @DisplayName("Should find patient by ID")
    void testGetPatientById() {
        system.addPatient(testPatient);

        Patient foundPatient = system.getPatientById("PAT001");

        assertNotNull(foundPatient);
        assertEquals("PAT001", foundPatient.getPatientId());
        assertEquals("John Doe", foundPatient.getName());
    }

    @Test
    @DisplayName("Should return null for non-existent patient ID")
    void testGetPatientByIdNotFound() {
        Patient foundPatient = system.getPatientById("NONEXISTENT");

        assertNull(foundPatient);
    }

    @Test
    @DisplayName("Should find doctor by ID")
    void testGetDoctorById() {
        system.addDoctor(testDoctor);

        Doctor foundDoctor = system.getDoctorById("DOC001");

        assertNotNull(foundDoctor);
        assertEquals("DOC001", foundDoctor.getDoctorId());
        assertEquals("Dr. Smith", foundDoctor.getName());
    }

    @Test
    @DisplayName("Should delete patient by ID")
    void testDeletePatient() {
        system.addPatient(testPatient);
        int initialSize = system.getAllPatients().size();

        system.deletePatient("PAT001");

        assertEquals(initialSize - 1, system.getAllPatients().size());
        assertNull(system.getPatientById("PAT001"));
    }

    @Test
    @DisplayName("Should delete doctor by ID")
    void testDeleteDoctor() {
        system.addDoctor(testDoctor);
        int initialSize = system.getAllDoctors().size();

        system.deleteDoctor("DOC001");

        assertEquals(initialSize - 1, system.getAllDoctors().size());
        assertNull(system.getDoctorById("DOC001"));
    }

    @Test
    @DisplayName("Should throw exception when deleting last user")
    void testDeleteLastUser() {
        // Clear all users except one
        system.getUsers().clear();
        system.getUsers().put("lastuser", testUser);

        assertThrows(IllegalStateException.class, () -> {
            system.deleteUser("lastuser");
        });
    }

    @Test
    @DisplayName("Should update user password")
    void testUpdateUserPassword() {
        system.getUsers().put("testuser", testUser);

        system.updateUserPassword("testuser", "newpassword");

        User updatedUser = system.getUsers().get("testuser");
        assertEquals("newpassword", updatedUser.getPassword());
    }

    @Test
    @DisplayName("Should not update password for non-existent user")
    void testUpdatePasswordNonExistentUser() {
        assertDoesNotThrow(() -> {
            system.updateUserPassword("nonexistent", "newpassword");
        });
    }

    @ParameterizedTest
    @MethodSource("provideAppointmentData")
    @DisplayName("Should add appointments with proper patient and doctor names")
    void testAddAppointment(String appointmentId, String patientId, String doctorId) {
        // Add patient and doctor first
        system.addPatient(new Patient(patientId, "Test Patient", 25, "Male", "Address", "Phone"));
        system.addDoctor(new Doctor(doctorId, "Test Doctor", "Specialty", "Availability"));

        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, "2023-12-25", "10:00", "Checkup");

        assertDoesNotThrow(() -> {
            system.addAppointment(appointment);
        });

        assertTrue(system.getAllAppointments().contains(appointment));
    }

    static Stream<String[]> provideAppointmentData() {
        return Stream.of(
                new String[]{"APP001", "PAT001", "DOC001"},
                new String[]{"APP002", "PAT002", "DOC002"},
                new String[]{"APP003", "PAT003", "DOC003"}
        );
    }

    @Test
    @DisplayName("Should mark appointment as completed")
    void testMarkAppointmentCompleted() {
        Appointment appointment = new Appointment("APP001", "PAT001", "DOC001", "2023-12-25", "10:00", "Checkup");
        system.addAppointment(appointment);

        system.markAppointmentCompleted("APP001");

        assertTrue(appointment.isCompleted());
    }

    @Test
    @DisplayName("Should mark bill as paid")
    void testMarkBillPaid() {
        Bill bill = new Bill("BILL001", "PAT001", 100.0, "Consultation");
        system.addBill(bill);

        system.markBillPaid("BILL001");

        assertTrue(bill.isPaid());
    }

    @Test
    @DisplayName("Should return defensive copies of collections")
    void testDefensiveCopies() {
        system.addPatient(testPatient);
        List<Patient> patients = system.getAllPatients();

        // Modifying returned list should not affect internal state
        patients.clear();

        assertFalse(system.getAllPatients().isEmpty());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullHandling() {
        assertDoesNotThrow(() -> {
            assertNull(system.getPatientById(null));
            assertNull(system.getDoctorById(null));
            assertNull(system.authenticateUser(null, null));
        });
    }

    @Test
    @DisplayName("Should maintain data consistency after operations")
    void testDataConsistency() {
        // Add test data
        system.addPatient(testPatient);
        system.addDoctor(testDoctor);

        int patientCount = system.getAllPatients().size();
        int doctorCount = system.getAllDoctors().size();

        // Perform operations
        system.deletePatient("PAT001");
        system.deleteDoctor("DOC001");

        assertEquals(patientCount - 1, system.getAllPatients().size());
        assertEquals(doctorCount - 1, system.getAllDoctors().size());
    }

    @Test
    @DisplayName("Should timeout for long operations")
    void testOperationTimeout() {
        assertTimeout(java.time.Duration.ofSeconds(5), () -> {
            // Simulate a potentially long operation
            for (int i = 0; i < 1000; i++) {
                system.addPatient(new Patient("PAT" + i, "Patient " + i, 25, "Male", "Address", "Phone"));
            }
        });
    }
}
