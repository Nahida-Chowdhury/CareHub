package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AdminDashboardTest {

    private AdminDashboard adminDashboard;

    @Mock
    private HospitalManagementSystem mockSystem;

    private Patient testPatient;
    private Doctor testDoctor;
    private Bill testBill;

    @BeforeAll
    static void setUpAll() {
        System.setProperty("java.awt.headless", "true");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("AdminDashboard tests completed");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        testPatient = new Patient("PAT001", "John Doe", 30, "Male", "123 Main St", "555-1234");
        testDoctor = new Doctor("DOC001", "Dr. Smith", "Cardiology", "9AM-5PM");
        testBill = new Bill("BILL001", "PAT001", 150.0, "Consultation");

        // Mock system behavior with default empty collections
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());
        when(mockSystem.getPatientById("PAT001")).thenReturn(testPatient);

        adminDashboard = new AdminDashboard(mockSystem);
    }

    @AfterEach
    void tearDown() {
        if (adminDashboard != null) {
            adminDashboard.removeAll();
        }
        reset(mockSystem);
    }

    @Test
    @DisplayName("Should initialize admin dashboard with all tabs")
    void testDashboardInitialization() {
        assertNotNull(adminDashboard);

        // Check if tabbed pane exists
        Component[] components = adminDashboard.getComponents();
        boolean hasTabbedPane = Arrays.stream(components)
                .anyMatch(c -> c instanceof JTabbedPane);

        assertTrue(hasTabbedPane);
    }

    @Test
    @DisplayName("Should refresh all panels when refreshData is called")
    void testRefreshData() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            adminDashboard.refreshData();
        });

        // Verify system methods were called
        verify(mockSystem, atLeastOnce()).getAllPatients();
        verify(mockSystem, atLeastOnce()).getAllDoctors();
        verify(mockSystem, atLeastOnce()).getAllBills();
        verify(mockSystem, atLeastOnce()).getUsers();
    }

    @Test
    @DisplayName("Should handle empty patient list")
    void testEmptyPatientList() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList());
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            adminDashboard.refreshData();
        });
    }

    @Test
    @DisplayName("Should handle null patient data gracefully")
    void testNullPatientData() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(null);
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            adminDashboard.refreshData();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 100})
    @DisplayName("Should handle different numbers of patients")
    void testVariousPatientCounts(int patientCount) {
        reset(mockSystem);

        List<Patient> patients = new java.util.ArrayList<>();
        for (int i = 0; i < patientCount; i++) {
            patients.add(new Patient("PAT" + i, "Patient " + i, 25 + i, "Male", "Address", "Phone"));
        }

        when(mockSystem.getAllPatients()).thenReturn(patients);
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            adminDashboard.refreshData();
        });
    }

    @Test
    @DisplayName("Should handle system exceptions gracefully")
    void testSystemExceptionHandling() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenThrow(new RuntimeException("Database error"));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            adminDashboard.refreshData();
        });
    }

    @Test
    @DisplayName("Should maintain component hierarchy")
    void testComponentHierarchy() {
        assertTrue(adminDashboard.getComponentCount() > 0);

        // Check for title label
        Component[] components = adminDashboard.getComponents();
        boolean hasTitle = Arrays.stream(components)
                .anyMatch(c -> c instanceof JLabel);

        assertTrue(hasTitle);
    }

    @Test
    @DisplayName("Should use BorderLayout")
    void testLayoutManager() {
        LayoutManager layout = adminDashboard.getLayout();
        assertTrue(layout instanceof BorderLayout);
    }

    @Test
    @DisplayName("Should handle concurrent refresh calls")
    void testConcurrentRefresh() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            // Simulate multiple refresh calls
            for (int i = 0; i < 10; i++) {
                adminDashboard.refreshData();
            }
        });
    }

    @Test
    @DisplayName("Should verify system integration")
    void testSystemIntegration() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        // Test that dashboard properly integrates with system
        adminDashboard.refreshData();

        verify(mockSystem, times(1)).getAllPatients();
        verify(mockSystem, times(1)).getAllDoctors();
        verify(mockSystem, times(1)).getAllBills();
        verify(mockSystem, times(1)).getUsers();
    }

    @Test
    @DisplayName("Should handle large datasets efficiently")
    void testLargeDatasetHandling() {
        reset(mockSystem);

        // Create large dataset
        List<Patient> largePatientList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largePatientList.add(new Patient("PAT" + i, "Patient " + i, 25, "Male", "Address", "Phone"));
        }

        when(mockSystem.getAllPatients()).thenReturn(largePatientList);
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertTimeout(java.time.Duration.ofSeconds(5), () -> {
            adminDashboard.refreshData();
        });
    }

    @Test
    @DisplayName("Should maintain state consistency")
    void testStateConsistency() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        // Initial state
        adminDashboard.refreshData();

        // Change mock data
        Patient newPatient = new Patient("PAT002", "Jane Doe", 25, "Female", "456 Oak St", "555-5678");
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient, newPatient));

        // Refresh and verify
        adminDashboard.refreshData();

        verify(mockSystem, times(2)).getAllPatients();
    }

    @Test
    @DisplayName("Should handle UI thread operations safely")
    void testUIThreadSafety() {
        reset(mockSystem);
        when(mockSystem.getAllPatients()).thenReturn(Arrays.asList(testPatient));
        when(mockSystem.getAllDoctors()).thenReturn(Arrays.asList(testDoctor));
        when(mockSystem.getAllBills()).thenReturn(Arrays.asList(testBill));
        when(mockSystem.getUsers()).thenReturn(new HashMap<>());

        assertDoesNotThrow(() -> {
            SwingUtilities.invokeAndWait(() -> {
                adminDashboard.refreshData();
            });
        });
    }

    @Test
    @DisplayName("Should properly dispose resources")
    void testResourceDisposal() {
        assertDoesNotThrow(() -> {
            adminDashboard.removeAll();
            adminDashboard.revalidate();
            adminDashboard.repaint();
        });
    }
}


