package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HospitalManagementSystem extends JFrame {
    // DAO instances for database operations
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private UserDAO userDAO;

    // In-memory cache for better performance (optional)
    private Map<String, User> users = new HashMap<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Bill> bills = new ArrayList<>();

    // Colors
    public final Color PRIMARY_COLOR = new Color(0, 123, 255);
    public final Color SECONDARY_COLOR = new Color(108, 117, 125);
    public final Color DARK_BG = new Color(33, 37, 41);

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

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.getInstance().closeConnection();
        }));
    }

    private void initializeDatabase() {
        try {
            // Test database connection
            if (!DatabaseConnection.getInstance().testConnection()) {
                JOptionPane.showMessageDialog(this,
                        "Failed to connect to database. Running in offline mode.",
                        "Database Connection Error",
                        JOptionPane.WARNING_MESSAGE);
                initializeOfflineData();
                return;
            }

            // Initialize DAO objects
            patientDAO = new PatientDAO();
            doctorDAO = new DoctorDAO();
            appointmentDAO = new AppointmentDAO();
            billDAO = new BillDAO();
            userDAO = new UserDAO();

            // Initialize default users if needed
            userDAO.initializeDefaultUsers();

            // Load data from database
            loadDataFromDatabase();

            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            initializeOfflineData();
        }
    }

    private void loadDataFromDatabase() {
        try {
            // Load users
            users = userDAO.getAllUsers();

            // Load patients
            patients = patientDAO.getAllPatients();

            // Load doctors
            doctors = doctorDAO.getAllDoctors();

            // Load appointments
            appointments = appointmentDAO.getAllAppointments();

            // Load bills
            bills = billDAO.getAllBills();

            // If no sample data exists, create some
            if (doctors.isEmpty()) {
                initializeSampleData();
            }

        } catch (Exception e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeSampleData() {
        try {
            // Add sample doctors
            Doctor doc1 = new Doctor("DOC1", "Dr. Smith", "Cardiology", "9AM-5PM");
            Doctor doc2 = new Doctor("DOC2", "Dr. Johnson", "Neurology", "10AM-6PM");
            Doctor doc3 = new Doctor("DOC3", "Dr. Williams", "Pediatrics", "8AM-4PM");

            doctorDAO.insertDoctor(doc1);
            doctorDAO.insertDoctor(doc2);
            doctorDAO.insertDoctor(doc3);
            doctors.add(doc1);
            doctors.add(doc2);
            doctors.add(doc3);

            // Add sample patients
            Patient pat1 = new Patient("PAT1", "John Doe", 35, "Male", "123 Main St", "555-1234");
            Patient pat2 = new Patient("PAT2", "Jane Smith", 28, "Female", "456 Oak Ave", "555-5678");
            Patient pat3 = new Patient("PAT3", "Robert Johnson", 45, "Male", "789 Pine Rd", "555-9012");

            patientDAO.insertPatient(pat1);
            patientDAO.insertPatient(pat2);
            patientDAO.insertPatient(pat3);
            patients.add(pat1);
            patients.add(pat2);
            patients.add(pat3);

            // Add sample appointments with patient and doctor names
            Appointment app1 = new Appointment("APP1", "PAT1", "DOC1", "2023-06-15", "10:00", "Regular checkup");
            app1.setNames(pat1, doc1); // Set the names
            Appointment app2 = new Appointment("APP2", "PAT2", "DOC2", "2023-06-15", "11:30", "Headache consultation");
            app2.setNames(pat2, doc2); // Set the names

            appointmentDAO.insertAppointment(app1);
            appointmentDAO.insertAppointment(app2);
            appointments.add(app1);
            appointments.add(app2);

            // Add sample bills
            Bill bill1 = new Bill("BILL1", "PAT1", 150.00, "Consultation fee");
            Bill bill2 = new Bill("BILL2", "PAT2", 200.00, "Lab tests");

            billDAO.insertBill(bill1);
            billDAO.insertBill(bill2);
            bills.add(bill1);
            bills.add(bill2);

            System.out.println("Sample data initialized in database.");
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeOfflineData() {
        // Fallback to in-memory data if database is not available
        users.put("admin", new User("admin", "admin123", UserRole.ADMIN));
        users.put("doctor1", new User("doctor1", "doc123", UserRole.DOCTOR));
        users.put("reception1", new User("reception1", "recep123", UserRole.RECEPTIONIST));

        doctors.add(new Doctor("DOC1", "Dr. Smith", "Cardiology", "9AM-5PM"));
        doctors.add(new Doctor("DOC2", "Dr. Johnson", "Neurology", "10AM-6PM"));
        doctors.add(new Doctor("DOC3", "Dr. Williams", "Pediatrics", "8AM-4PM"));

        patients.add(new Patient("PAT1", "John Doe", 35, "Male", "123 Main St", "555-1234"));
        patients.add(new Patient("PAT2", "Jane Smith", 28, "Female", "456 Oak Ave", "555-5678"));
        patients.add(new Patient("PAT3", "Robert Johnson", 45, "Male", "789 Pine Rd", "555-9012"));

        // Create appointments with names
        Appointment app1 = new Appointment("APP1", "PAT1", "DOC1", "2023-06-15", "10:00", "Regular checkup");
        app1.setNames("John Doe", "Dr. Smith");
        Appointment app2 = new Appointment("APP2", "PAT2", "DOC2", "2023-06-15", "11:30", "Headache consultation");
        app2.setNames("Jane Smith", "Dr. Johnson");

        appointments.add(app1);
        appointments.add(app2);

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
    public void showLoginPanel() {
        loginPanel.clearFields();
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showAdminDashboard() {
        refreshDataFromDatabase();
        adminDashboard.refreshData();
        cardLayout.show(mainPanel, "ADMIN");
    }

    public void showDoctorDashboard() {
        refreshDataFromDatabase();
        doctorDashboard.refreshData();
        cardLayout.show(mainPanel, "DOCTOR");
    }

    public void showReceptionistDashboard() {
        refreshDataFromDatabase();
        receptionistDashboard.refreshData();
        cardLayout.show(mainPanel, "RECEPTIONIST");
    }

    // Refresh data from database
    private void refreshDataFromDatabase() {
        if (patientDAO != null) {
            try {
                System.out.println("Refreshing data from database...");

                // Clear existing data first
                patients.clear();
                doctors.clear();
                appointments.clear();
                bills.clear();
                users.clear();

                // Load fresh data from database
                patients.addAll(patientDAO.getAllPatients());
                doctors.addAll(doctorDAO.getAllDoctors());
                appointments.addAll(appointmentDAO.getAllAppointments());
                bills.addAll(billDAO.getAllBills());
                users.putAll(userDAO.getAllUsers());

                System.out.println("Data refreshed - Patients: " + patients.size() +
                        ", Doctors: " + doctors.size() +
                        ", Appointments: " + appointments.size() +
                        ", Bills: " + bills.size() +
                        ", Users: " + users.size());
            } catch (Exception e) {
                System.err.println("Error refreshing data from database: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("DAO is null, cannot refresh from database");
        }
    }

    // Add a public method to manually refresh data
    public void refreshAllData() {
        refreshDataFromDatabase();

        // Refresh all dashboard UIs
        if (adminDashboard != null) {
            adminDashboard.refreshData();
        }
        if (doctorDashboard != null) {
            doctorDashboard.refreshData();
        }
        if (receptionistDashboard != null) {
            receptionistDashboard.refreshData();
        }
    }

    // Authentication
    public User authenticateUser(String username, String password) {
        if (userDAO != null) {
            return userDAO.authenticateUser(username, password);
        } else {
            // Fallback to in-memory authentication
            User user = users.get(username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
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

    // CRUD operations with database integration
    public void addPatient(Patient patient) {
        patients.add(patient);
        if (patientDAO != null) {
            boolean success = patientDAO.insertPatient(patient);
            if (success) {
                System.out.println("Patient added successfully, refreshing data...");
                refreshDataFromDatabase(); // Refresh to get latest data
            }
        }
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        if (doctorDAO != null) {
            boolean success = doctorDAO.insertDoctor(doctor);
            if (success) {
                System.out.println("Doctor added successfully, refreshing data...");
                refreshDataFromDatabase();
            }
        }
    }

    public void addAppointment(Appointment appointment) {
        // Ensure names are set before adding to database
        if (appointment.getPatientName() == null || appointment.getDoctorName() == null) {
            Patient patient = getPatientById(appointment.getPatientId());
            Doctor doctor = getDoctorById(appointment.getDoctorId());
            appointment.setNames(patient, doctor);
        }

        appointments.add(appointment);
        if (appointmentDAO != null) {
            boolean success = appointmentDAO.insertAppointment(appointment);
            if (success) {
                System.out.println("Appointment added successfully, refreshing data...");
                refreshDataFromDatabase();
            }
        }
    }

    public void addBill(Bill bill) {
        bills.add(bill);
        if (billDAO != null) {
            boolean success = billDAO.insertBill(bill);
            if (success) {
                System.out.println("Bill added successfully, refreshing data...");
                refreshDataFromDatabase();
            }
        }
    }

    public void deletePatient(String id) {
        patients.removeIf(p -> p.getPatientId().equals(id));
        if (patientDAO != null) {
            boolean success = patientDAO.deletePatient(id);
            if (success) {
                System.out.println("Patient deleted successfully, refreshing data...");
                refreshDataFromDatabase();
            }
        }
    }

    public void deleteDoctor(String id) {
        doctors.removeIf(d -> d.getDoctorId().equals(id));
        if (doctorDAO != null) {
            boolean success = doctorDAO.deleteDoctor(id);
            if (success) {
                System.out.println("Doctor deleted successfully, refreshing data...");
                refreshDataFromDatabase();
            }
        }
    }

    public void markAppointmentCompleted(String id) {
        appointments.stream()
                .filter(a -> a.getAppointmentId().equals(id))
                .findFirst()
                .ifPresent(a -> {
                    a.setCompleted(true);
                    if (appointmentDAO != null) {
                        appointmentDAO.markAppointmentCompleted(id);
                    }
                });
    }

    public void markBillPaid(String id) {
        bills.stream()
                .filter(b -> b.getBillId().equals(id))
                .findFirst()
                .ifPresent(b -> {
                    b.setPaid(true);
                    if (billDAO != null) {
                        billDAO.markBillPaid(id);
                    }
                });
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void deleteUser(String username) {
        if (users.size() <= 1) {
            throw new IllegalStateException("Cannot delete last user");
        }
        users.remove(username);
        if (userDAO != null) {
            userDAO.deleteUser(username);
        }
    }

    public void updateUserPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            User updatedUser = new User(username, newPassword, user.getRole());
            users.put(username, updatedUser);
            if (userDAO != null) {
                userDAO.updateUserPassword(username, newPassword);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HospitalManagementSystem().setVisible(true);
        });
    }

    // Custom styled components (keeping existing styling)
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
            getTableHeader().setOpaque(false); // Ensure the background is drawn
            setFillsViewportHeight(true);
        }
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }
}
