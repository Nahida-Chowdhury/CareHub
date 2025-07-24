package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class HospitalManagementSystem extends JFrame {
    // Database
    private Map<String, User> users = new HashMap<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Bill> bills = new ArrayList<>();

    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color DANGER_COLOR = new Color(220, 53, 69);
    private final Color LIGHT_BG = new Color(248, 249, 250);
    private final Color DARK_BG = new Color(33, 37, 41);

    // GUI Components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private LoginPanel loginPanel;
    private AdminDashboard adminDashboard;
    private DoctorDashboard doctorDashboard;
    private ReceptionistDashboard receptionistDashboard;

    public HospitalManagementSystem() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeDatabase();
        createPanels();

        add(mainPanel);
        showLoginPanel();
    }

    private void initializeDatabase() {
        // Sample users
        users.put("admin", new User("admin", "admin123", UserRole.ADMIN));
        users.put("doctor1", new User("doctor1", "doc123", UserRole.DOCTOR));
        users.put("reception1", new User("reception1", "recep123", UserRole.RECEPTIONIST));

        // Samples
        doctors.add(new Doctor("DOC1", "Dr. Smith", "Cardiology", "9AM-5PM"));
        doctors.add(new Doctor("DOC2", "Dr. Johnson", "Neurology", "10AM-6PM"));
        doctors.add(new Doctor("DOC3", "Dr. Williams", "Pediatrics", "8AM-4PM"));

        patients.add(new Patient("PAT1", "John Doe", 35, "Male", "123 Main St", "555-1234"));
        patients.add(new Patient("PAT2", "Jane Smith", 28, "Female", "456 Oak Ave", "555-5678"));
        patients.add(new Patient("PAT3", "Robert Johnson", 45, "Male", "789 Pine Rd", "555-9012"));

        appointments.add(new Appointment("APP1", "PAT1", "DOC1", "2023-06-15", "10:00", "Regular checkup"));
        appointments.add(new Appointment("APP2", "PAT2", "DOC2", "2023-06-15", "11:30", "Headache consultation"));

        bills.add(new Bill("BILL1", "PAT1", 150.00, "Consultation fee"));
        bills.add(new Bill("BILL2", "PAT2", 200.00, "Lab tests"));
    }

    private void createPanels() {
        loginPanel = new LoginPanel(this);
        adminDashboard = new AdminDashboard(this);
        doctorDashboard = new DoctorDashboard(this);
        receptionistDashboard = new ReceptionistDashboard(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(adminDashboard, "ADMIN");
        mainPanel.add(doctorDashboard, "DOCTOR");
        mainPanel.add(receptionistDashboard, "RECEPTIONIST");
    }

    // Navigation methods
    public void showLoginPanel() { cardLayout.show(mainPanel, "LOGIN"); }
    public void showAdminDashboard() { adminDashboard.refreshData(); cardLayout.show(mainPanel, "ADMIN"); }
    public void showDoctorDashboard() { doctorDashboard.refreshData(); cardLayout.show(mainPanel, "DOCTOR"); }
    public void showReceptionistDashboard() { receptionistDashboard.refreshData(); cardLayout.show(mainPanel, "RECEPTIONIST"); }

    // Authentication
    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Data access methods
    public List<Patient> getAllPatients() { return new ArrayList<>(patients); }
    public List<Doctor> getAllDoctors() { return new ArrayList<>(doctors); }
    public List<Appointment> getAllAppointments() { return new ArrayList<>(appointments); }
    public List<Bill> getAllBills() { return new ArrayList<>(bills); }

    public Patient getPatientById(String id) {
        return patients.stream().filter(p -> p.getPatientId().equals(id)).findFirst().orElse(null);
    }

    public Doctor getDoctorById(String id) {
        return doctors.stream().filter(d -> d.getDoctorId().equals(id)).findFirst().orElse(null);
    }

    // CRUD operations
    public void addPatient(Patient patient) { patients.add(patient); }
    public void addDoctor(Doctor doctor) { doctors.add(doctor); }
    public void addAppointment(Appointment appointment) { appointments.add(appointment); }
    public void addBill(Bill bill) { bills.add(bill); }

    public void deletePatient(String id) { patients.removeIf(p -> p.getPatientId().equals(id)); }
    public void deleteDoctor(String id) { doctors.removeIf(d -> d.getDoctorId().equals(id)); }

    public void markAppointmentCompleted(String id) {
        appointments.stream()
                .filter(a -> a.getAppointmentId().equals(id))
                .findFirst()
                .ifPresent(a -> a.setCompleted(true));
    }

    public void markBillPaid(String id) {
        bills.stream()
                .filter(b -> b.getBillId().equals(id))
                .findFirst()
                .ifPresent(b -> b.setPaid(true));
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void deleteUser(String username) {
        if (users.size() <= 1) {
            throw new IllegalStateException("Cannot delete last user");
        }
        users.remove(username);
    }

    public void updateUserPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            users.put(username, new User(username, newPassword, user.getRole()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HospitalManagementSystem().setVisible(true);
        });
    }

    // Custom styled button
    class StyledButton extends JButton {
        public StyledButton(String text, Color bgColor) {
            super(text);
            setBackground(bgColor);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    class StyledTextField extends JTextField {
        public StyledTextField(int columns) {
            super(columns);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    class StyledComboBox<T> extends JComboBox<T> {
        public StyledComboBox() {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value != null) {
                        setText(value.toString());
                    }
                    return this;
                }
            });
        }
    }

    class StyledTable extends JTable {
        public StyledTable(DefaultTableModel model) {
            super(model);
            setShowGrid(false);
            setIntercellSpacing(new Dimension(0, 0));
            setRowHeight(40);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            getTableHeader().setBackground(DARK_BG);
            getTableHeader().setForeground(Color.WHITE);
            setSelectionBackground(PRIMARY_COLOR);
            setSelectionForeground(Color.WHITE);
            setFillsViewportHeight(true);
        }
    }
}