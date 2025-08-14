package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Using PER_CLASS lifecycle to allow static @BeforeAll and @AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReceptionistDashboardTest {

    // Mocked dependencies
    @Mock
    private HospitalManagementSystem mockSystem;
    @Mock
    private Patient mockPatient;
    @Mock
    private Doctor mockDoctor;
    @Mock
    private Appointment mockAppointment;
    @Mock
    private Bill mockBill;

    // The class under test
    private ReceptionistDashboard receptionistDashboard;
    private AutoCloseable closeable;

    private final List<Appointment> mockAppointments = new ArrayList<>();
    private final List<Bill> mockBills = new ArrayList<>();
    private final List<Patient> mockPatients = new ArrayList<>();
    private final List<Doctor> mockDoctors = new ArrayList<>();

    // This runs once before any of the tests in this class
    @BeforeAll
    static void setupAll() {
        System.out.println("Starting all tests for ReceptionistDashboardTest class.");
    }

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Initialize mocks
        closeable = MockitoAnnotations.openMocks(this);

        // --- FIX: Clear mock lists to ensure test isolation ---
        mockAppointments.clear();
        mockBills.clear();
        mockPatients.clear();
        mockDoctors.clear();

        // Populate mock data
        when(mockPatient.getPatientId()).thenReturn("P001");
        when(mockPatient.getName()).thenReturn("John Doe");

        when(mockDoctor.getDoctorId()).thenReturn("D001");
        when(mockDoctor.getName()).thenReturn("Dr. Smith");
        when(mockDoctor.getSpecialization()).thenReturn("Cardiology");
        when(mockDoctor.toString()).thenReturn("Dr. Smith (Cardiology)");

        when(mockAppointment.getAppointmentId()).thenReturn("A001");
        when(mockAppointment.getPatientId()).thenReturn("P001");
        when(mockAppointment.getDoctorId()).thenReturn("D001");
        when(mockAppointment.getDate()).thenReturn("2024-08-15");
        when(mockAppointment.getTime()).thenReturn("10:00 AM");
        when(mockAppointment.getDescription()).thenReturn("General Checkup");
        when(mockAppointment.isCompleted()).thenReturn(false);

        when(mockBill.getBillId()).thenReturn("B001");
        when(mockBill.getPatientId()).thenReturn("P001");
        when(mockBill.getAmount()).thenReturn(150.00);
        when(mockBill.getDescription()).thenReturn("Consultation fee");
        when(mockBill.isPaid()).thenReturn(false);

        mockAppointments.add(mockAppointment);
        mockBills.add(mockBill);
        mockPatients.add(mockPatient);
        mockDoctors.add(mockDoctor);

        // Configure mock system's behavior
        when(mockSystem.getAllAppointments()).thenReturn(mockAppointments);
        when(mockSystem.getAllBills()).thenReturn(mockBills);
        when(mockSystem.getAllPatients()).thenReturn(mockPatients);
        when(mockSystem.getAllDoctors()).thenReturn(mockDoctors);
        when(mockSystem.getPatientById("P001")).thenReturn(mockPatient);
        when(mockSystem.getDoctorById("D001")).thenReturn(mockDoctor);

        // Create the dashboard instance
        receptionistDashboard = new ReceptionistDashboard(mockSystem);
    }

    // This runs after each test
    @AfterEach
    void tearDown() throws Exception {
        // Close mocks to release resources
        closeable.close();
    }

    // This runs once after all the tests in this class have finished
    @AfterAll
    static void tearDownAll() {
        System.out.println("All tests for ReceptionistDashboardTest class have finished.");
    }

    // Test to ensure the main dashboard panel is initialized correctly.
    @Test
    void testDashboardInitialization() {
        assertNotNull(receptionistDashboard, "Dashboard should be initialized");
        assertTrue(receptionistDashboard.getLayout() instanceof BorderLayout, "Dashboard should use BorderLayout");

        // --- FIX: Use a more reliable way to find the label by its text content, not its name ---
        JLabel titleLabel = findLabelByText(receptionistDashboard, "Receptionist Dashboard");
        assertNotNull(titleLabel, "Dashboard title label should exist");
        assertNotNull(findComponentOfType(receptionistDashboard, JTabbedPane.class), "Tabbed pane should exist");
    }

    // Test to verify that the refreshData method calls refresh on all tabs.
    @Test
    void testRefreshDataCallsAllTabs() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                receptionistDashboard.refreshData();
                verify(mockSystem, atLeast(1)).getAllAppointments();
                verify(mockSystem, atLeast(1)).getAllBills();
            });
        } catch (Exception e) {
            fail("Exception occurred during refresh test: " + e.getMessage());
        }
    }

    // Mockito Test: Verify that the appointment table is populated correctly after a refresh.
    @Test
    void testAppointmentPanelRefreshData() {
        // Get the JTabbedPane and then the AppointmentManagementPanel from it.
        JTabbedPane tabbedPane = (JTabbedPane) receptionistDashboard.getComponent(1);
        ReceptionistDashboard.AppointmentManagementPanel appointmentPanel = (ReceptionistDashboard.AppointmentManagementPanel) tabbedPane.getComponentAt(0);
        DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane) appointmentPanel.getComponent(0)).getViewport().getView()).getModel();

        try {
            SwingUtilities.invokeAndWait(() -> {
                appointmentPanel.refreshData();
            });
        } catch (Exception e) {
            fail("Exception occurred during appointment panel refresh test: " + e.getMessage());
        }

        assertEquals(1, model.getRowCount(), "Table should have one row after refresh");
        assertEquals("A001", model.getValueAt(0, 0), "Appointment ID should be correct");
        assertEquals("John Doe", model.getValueAt(0, 1), "Patient name should be resolved from ID");
        assertEquals("Dr. Smith", model.getValueAt(0, 2), "Doctor name should be resolved from ID");
        assertEquals("Pending", model.getValueAt(0, 6), "Status should be pending");
    }

    // Mockito Test: Verify that the billing table is populated correctly after a refresh.
    @Test
    void testBillingPanelRefreshData() {
        // Get the JTabbedPane and then the BillingManagementPanel from it.
        JTabbedPane tabbedPane = (JTabbedPane) receptionistDashboard.getComponent(1);
        ReceptionistDashboard.BillingManagementPanel billingPanel = (ReceptionistDashboard.BillingManagementPanel) tabbedPane.getComponentAt(1);
        DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane) billingPanel.getComponent(0)).getViewport().getView()).getModel();

        try {
            SwingUtilities.invokeAndWait(() -> {
                billingPanel.refreshData();
            });
        } catch (Exception e) {
            fail("Exception occurred during billing panel refresh test: " + e.getMessage());
        }

        assertEquals(1, model.getRowCount(), "Table should have one row after refresh");
        assertEquals("B001", model.getValueAt(0, 0), "Bill ID should be correct");
        assertEquals("John Doe", model.getValueAt(0, 1), "Patient name should be resolved from ID");
        assertEquals("$150.00", model.getValueAt(0, 2), "Amount should be correctly formatted");
        assertEquals("Pending", model.getValueAt(0, 4), "Status should be pending");
    }

    // --- NEW: Helper method to find a label by its text ---
    private JLabel findLabelByText(Container container, String text) {
        return (JLabel) Stream.of(container.getComponents())
                .filter(c -> c instanceof JLabel && ((JLabel) c).getText().equals(text))
                .findFirst()
                .orElse(null);
    }

    // --- NEW: Helper method to find a button by its text ---
    private JButton findButtonByText(Container container, String text) {
        return (JButton) Stream.of(container.getComponents())
                .filter(c -> c instanceof JPanel)
                .flatMap(p -> Stream.of(((JPanel) p).getComponents()))
                .filter(c -> c instanceof JButton && ((JButton) c).getText().equals(text))
                .findFirst()
                .orElse(null);
    }

    // Helper method to find a component by its name
    private Component findComponentByName(Container container, String name) {
        if (name.equals(container.getName())) {
            return container;
        }
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Helper method to find a component by its type
    private Component findComponentOfType(Container container, Class<?> type) {
        if (type.isInstance(container)) {
            return container;
        }
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentOfType((Container) component, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}