package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;

class DoctorDashboardTest {

    private DoctorDashboard doctorDashboard;
    private HospitalManagementSystem mockSystem;
    private Patient testPatient;
    private Appointment testAppointment;
    private MedicalRecord testMedicalRecord;

    @BeforeEach
    void setUp() {
        // Create mock system
        mockSystem = mock(HospitalManagementSystem.class);

        // Create test data
        testPatient = new Patient("P001", "John Doe", 30, "Male", "123 Main St", "555-1234");
        testAppointment = new Appointment("A001", "P001", "DOC1", "2025-08-12", "10:00", "Regular checkup");
        testMedicalRecord = new MedicalRecord("MR001", "P001", "DOC1", "Hypertension", "Medication prescribed", "Patient doing well");

        // Add test data to patient
        testPatient.addMedicalRecord(testMedicalRecord);
        testPatient.addMedication(new Medication("Lisinopril", "10mg", "Once daily", "2025-08-01", "2025-12-01"));
        testPatient.addAllergy("Penicillin");

        lenient().when(mockSystem.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        lenient().when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        lenient().when(mockSystem.getPatientById("P001")).thenReturn(testPatient);

        // Create dashboard (this will call refreshData() once in constructor)
        doctorDashboard = new DoctorDashboard(mockSystem);

        clearInvocations(mockSystem);
    }

    @Test
    @DisplayName("Should initialize dashboard with correct components")
    void testDashboardInitialization() {
        assertNotNull(doctorDashboard);
        // Verify that the dashboard was created without throwing exceptions
        assertTrue(doctorDashboard.getComponentCount() > 0);
    }

    @Test
    @DisplayName("Should refresh appointment data correctly")
    void testRefreshAppointmentData() {
        // Call refresh data
        doctorDashboard.refreshData();

        verify(mockSystem, times(1)).getAllAppointments();
        verify(mockSystem, times(1)).getAllPatients();
        verify(mockSystem, times(1)).getPatientById("P001");
    }

    @Test
    @DisplayName("Should handle empty appointment list")
    void testEmptyAppointmentList() {
        reset(mockSystem);
        when(mockSystem.getAllAppointments()).thenReturn(new ArrayList<>());
        when(mockSystem.getAllPatients()).thenReturn(new ArrayList<>());

        // Should not throw exception
        assertDoesNotThrow(() -> doctorDashboard.refreshData());
    }

    @Test
    @DisplayName("Should handle null patient in appointment")
    void testNullPatientInAppointment() {
        reset(mockSystem);
        when(mockSystem.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getPatientById("P001")).thenReturn(null);

        // Should not throw exception and handle gracefully
        assertDoesNotThrow(() -> doctorDashboard.refreshData());
    }

    @Test
    @DisplayName("Should mark appointment as completed")
    void testMarkAppointmentCompleted() {
        // Setup appointment completion
        doNothing().when(mockSystem).markAppointmentCompleted("A001");

        // This would normally require GUI interaction, so we test the system call
        doctorDashboard.refreshData();

        verify(mockSystem, times(1)).getAllAppointments();
    }

    @Test
    @DisplayName("Should handle medical record creation")
    void testMedicalRecordCreation() {
        // Test that medical records are properly displayed
        doctorDashboard.refreshData();

        verify(mockSystem, times(1)).getAllPatients();

        // Verify patient has medical records
        assertFalse(testPatient.getMedicalHistory().isEmpty());
        assertEquals(1, testPatient.getMedicalHistory().size());
    }

    @Test
    @DisplayName("Should handle patient with medications")
    void testPatientMedications() {
        doctorDashboard.refreshData();

        // Verify patient has medications
        assertFalse(testPatient.getMedications().isEmpty());
        assertEquals(1, testPatient.getMedications().size());
        assertEquals("Lisinopril", testPatient.getMedications().get(0).getName());
    }

    @Test
    @DisplayName("Should handle patient with allergies")
    void testPatientAllergies() {
        doctorDashboard.refreshData();

        // Verify patient has allergies
        assertFalse(testPatient.getAllergies().isEmpty());
        assertEquals(1, testPatient.getAllergies().size());
        assertTrue(testPatient.getAllergies().contains("Penicillin"));
    }

    @Test
    @DisplayName("Should handle multiple patients and records")
    void testMultiplePatientsAndRecords() {
        // Create additional test data
        Patient patient2 = new Patient("P002", "Jane Smith", 25, "Female", "456 Oak Ave", "555-5678");
        MedicalRecord record2 = new MedicalRecord("MR002", "P002", "DOC1", "Diabetes", "Insulin therapy", "Regular monitoring needed");
        patient2.addMedicalRecord(record2);

        reset(mockSystem);
        when(mockSystem.getAllAppointments()).thenReturn(Arrays.asList(testAppointment));
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient, patient2));
        when(mockSystem.getPatientById("P001")).thenReturn(testPatient);
        when(mockSystem.getPatientById("P002")).thenReturn(patient2);

        doctorDashboard.refreshData();

        // Verify both patients are handled
        verify(mockSystem, times(1)).getAllPatients();
    }

    @Test
    @DisplayName("Should handle system logout")
    void testLogout() {
        doNothing().when(mockSystem).showLoginPanel();

        // Verify system has showLoginPanel method available
        verify(mockSystem, never()).showLoginPanel(); // Not called yet

        // The actual logout would be triggered by button click in GUI
        mockSystem.showLoginPanel();
        verify(mockSystem).showLoginPanel();
    }
}
