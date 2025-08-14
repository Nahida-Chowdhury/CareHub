package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

// Admin Dashboard
class AdminDashboard extends JPanel {
    private HospitalManagementSystem system;
    private JTabbedPane tabbedPane;

    public AdminDashboard(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Administrator Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.PAGE_START);

        // Tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font for tabs
        tabbedPane.addTab("Patients", new PatientManagementPanel());
        tabbedPane.addTab("Doctors", new DoctorManagementPanel());
        tabbedPane.addTab("Billing", new BillingManagementPanel());
        tabbedPane.addTab("Users", new UserManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        System.out.println("Admin Dashboard - refreshing all panels...");
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof Refreshable) {
                ((Refreshable) comp).refreshData();
            }
        }
        System.out.println("Admin Dashboard - all panels refreshed");
    }

    interface Refreshable {
        void refreshData();
    }

    class PatientManagementPanel extends JPanel implements Refreshable {
        private DefaultTableModel tableModel;
        private JTable patientTable;

        public PatientManagementPanel() {
            setLayout(new BorderLayout());

            // Table setup
            String[] columns = {"ID", "Name", "Age", "Gender", "Phone"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            patientTable = system.new StyledTable(tableModel); // Use StyledTable
            JScrollPane scrollPane = new JScrollPane(patientTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Patient");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showPatientDialog(null));
            editButton.addActionListener(e -> editPatient());
            deleteButton.addActionListener(e -> deletePatient());
            refreshButton.addActionListener(e -> {
                System.out.println("Patient Refresh button clicked - refreshing patient data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            System.out.println("Refreshing patient table data...");
            tableModel.setRowCount(0);
            List<Patient> currentPatients = system.getAllPatients();
            System.out.println("Found " + currentPatients.size() + " patients");

            for (Patient p : currentPatients) {
                tableModel.addRow(new Object[]{
                        p.getPatientId(), p.getName(), p.getAge(), p.getGender(), p.getPhone()
                });
            }

            tableModel.fireTableDataChanged();
            patientTable.revalidate();
            patientTable.repaint();
            System.out.println("Patient table refreshed");
        }

        private void showPatientDialog(Patient patient) {
            JDialog dialog = new JDialog();
            dialog.setTitle(patient == null ? "Add Patient" : "Edit Patient");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            JTextField phoneField = new JTextField();
            JTextField addressField = new JTextField();

            if (patient != null) {
                idField.setText(patient.getPatientId());
                nameField.setText(patient.getName());
                ageField.setText(String.valueOf(patient.getAge()));
                genderCombo.setSelectedItem(patient.getGender());
                phoneField.setText(patient.getPhone());
                addressField.setText(patient.getAddress());
                idField.setEditable(false);
            } else {
                idField.setText(IDGenerator.generatePatientID());
                idField.setEditable(false);
            }

            dialog.add(new JLabel("Patient ID:"));
            dialog.add(idField);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);
            dialog.add(new JLabel("Age:"));
            dialog.add(ageField);
            dialog.add(new JLabel("Gender:"));
            dialog.add(genderCombo);
            dialog.add(new JLabel("Phone:"));
            dialog.add(phoneField);
            dialog.add(new JLabel("Address:"));
            dialog.add(addressField);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient p = new Patient(
                            idField.getText(),
                            nameField.getText(),
                            Integer.parseInt(ageField.getText()),
                            genderCombo.getSelectedItem().toString(),
                            addressField.getText(),
                            phoneField.getText()
                    );

                    if (patient == null) {
                        System.out.println("Adding new patient: " + p.getName());
                        system.addPatient(p);
                    } else {
                        System.out.println("Updating patient: " + p.getName());
                        system.deletePatient(patient.getPatientId());
                        system.addPatient(p);
                    }
                    refreshData();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid age", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JLabel());
            dialog.add(buttonPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        private void editPatient() {
            int row = patientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            showPatientDialog(system.getPatientById(id));
        }

        private void deletePatient() {
            int row = patientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete patient " + name + " (" + id + ")?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Deleting patient: " + name + " (" + id + ")");
                system.deletePatient(id);
                refreshData();
            }
        }
    }

    class DoctorManagementPanel extends JPanel implements Refreshable {
        private DefaultTableModel tableModel;
        private JTable doctorTable;

        public DoctorManagementPanel() {
            setLayout(new BorderLayout());

            // Table setup
            String[] columns = {"ID", "Name", "Specialization", "Availability"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            doctorTable = system.new StyledTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(doctorTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Doctor");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showDoctorDialog(null));
            editButton.addActionListener(e -> editDoctor());
            deleteButton.addActionListener(e -> deleteDoctor());
            refreshButton.addActionListener(e -> {
                System.out.println("Doctor Refresh button clicked - refreshing doctor data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            System.out.println("Refreshing doctor table data...");
            tableModel.setRowCount(0);
            List<Doctor> currentDoctors = system.getAllDoctors();
            System.out.println("Found " + currentDoctors.size() + " doctors");

            for (Doctor d : currentDoctors) {
                tableModel.addRow(new Object[]{
                        d.getDoctorId(), d.getName(), d.getSpecialization(), d.getAvailability()
                });
            }

            tableModel.fireTableDataChanged();
            doctorTable.revalidate();
            doctorTable.repaint();
            System.out.println("Doctor table refreshed");
        }

        private void showDoctorDialog(Doctor doctor) {
            JDialog dialog = new JDialog();
            dialog.setTitle(doctor == null ? "Add Doctor" : "Edit Doctor");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField specField = new JTextField();
            JTextField availField = new JTextField();

            if (doctor != null) {
                idField.setText(doctor.getDoctorId());
                nameField.setText(doctor.getName());
                specField.setText(doctor.getSpecialization());
                availField.setText(doctor.getAvailability());
                idField.setEditable(false);
            } else {
                idField.setText(IDGenerator.generateDoctorID());
                idField.setEditable(false);
            }

            dialog.add(new JLabel("Doctor ID:"));
            dialog.add(idField);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);
            dialog.add(new JLabel("Specialization:"));
            dialog.add(specField);
            dialog.add(new JLabel("Availability:"));
            dialog.add(availField);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                Doctor d = new Doctor(
                        idField.getText(),
                        nameField.getText(),
                        specField.getText(),
                        availField.getText()
                );

                if (doctor == null) {
                    System.out.println("Adding new doctor: " + d.getName());
                    system.addDoctor(d);
                } else {
                    System.out.println("Updating doctor: " + d.getName());
                    system.deleteDoctor(doctor.getDoctorId());
                    system.addDoctor(d);
                }
                refreshData();
                dialog.dispose();
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JLabel());
            dialog.add(buttonPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        private void editDoctor() {
            int row = doctorTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            showDoctorDialog(system.getDoctorById(id));
        }

        private void deleteDoctor() {
            int row = doctorTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete doctor " + name + " (" + id + ")?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Deleting doctor: " + name + " (" + id + ")");
                system.deleteDoctor(id);
                refreshData();
            }
        }
    }

    class BillingManagementPanel extends JPanel implements Refreshable {
        private DefaultTableModel tableModel;
        private JTable billTable;

        public BillingManagementPanel() {
            setLayout(new BorderLayout());

            // Table setup
            String[] columns = {"ID", "Patient", "Amount", "Description", "Status"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            billTable = system.new StyledTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(billTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("New Bill");
            JButton payButton = new JButton("Mark Paid");
            JButton deleteButton = new JButton("Delete");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showBillDialog());
            payButton.addActionListener(e -> markBillPaid());
            deleteButton.addActionListener(e -> deleteBill());
            refreshButton.addActionListener(e -> {
                System.out.println("Billing Refresh button clicked - refreshing billing data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });

            buttonPanel.add(addButton);
            buttonPanel.add(payButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            System.out.println("Refreshing billing table data...");
            tableModel.setRowCount(0);
            List<Bill> currentBills = system.getAllBills();
            System.out.println("Found " + currentBills.size() + " bills");

            for (Bill b : currentBills) {
                Patient p = system.getPatientById(b.getPatientId());
                tableModel.addRow(new Object[]{
                        b.getBillId(),
                        p != null ? p.getName() : "Unknown",
                        String.format("$%.2f", b.getAmount()),
                        b.getDescription(),
                        b.isPaid() ? "Paid" : "Pending"
                });
            }

            tableModel.fireTableDataChanged();
            billTable.revalidate();
            billTable.repaint();
            System.out.println("Billing table refreshed");
        }

        private void showBillDialog() {
            JDialog dialog = new JDialog();
            dialog.setTitle("New Bill");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            JComboBox<Patient> patientCombo = new JComboBox<>();
            JTextField amountField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);

            // Populate patient combo
            for (Patient p : system.getAllPatients()) {
                patientCombo.addItem(p);
            }

            dialog.add(new JLabel("Patient:"));
            dialog.add(patientCombo);
            dialog.add(new JLabel("Amount:"));
            dialog.add(amountField);
            dialog.add(new JLabel("Description:"));
            dialog.add(new JScrollPane(descArea));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient p = (Patient) patientCombo.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());
                    String desc = descArea.getText();

                    if (p == null || desc.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    String id = IDGenerator.generateBillID();
                    Bill newBill = new Bill(id, p.getPatientId(), amount, desc);
                    System.out.println("Creating new bill for patient: " + p.getName() + " - Amount: $" + amount);
                    system.addBill(newBill);
                    refreshData();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid amount", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JLabel());
            dialog.add(buttonPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        private void markBillPaid() {
            int row = billTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a bill", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String patientName = (String) tableModel.getValueAt(row, 1);
            System.out.println("Marking bill as paid: " + id + " for patient: " + patientName);
            system.markBillPaid(id);
            refreshData();
        }

        private void deleteBill() {
            int row = billTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a bill", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String patientName = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete bill " + id + " for " + patientName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Deleting bill: " + id + " for patient: " + patientName);
                system.getAllBills().removeIf(b -> b.getBillId().equals(id));
                refreshData();
            }
        }
    }

    class UserManagementPanel extends JPanel implements Refreshable {
        private DefaultTableModel tableModel;
        private JTable userTable;

        public UserManagementPanel() {
            setLayout(new BorderLayout());

            // Table setup
            String[] columns = {"Username", "Role", "Password"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            userTable = system.new StyledTable(tableModel);
            add(new JScrollPane(userTable), BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add User");
            JButton deleteButton = new JButton("Delete");
            JButton resetPassButton = new JButton("Reset Password");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showUserDialog(null));
            deleteButton.addActionListener(e -> deleteUser());
            resetPassButton.addActionListener(e -> resetPassword());
            refreshButton.addActionListener(e -> {
                System.out.println("User Refresh button clicked - refreshing user data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });

            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(resetPassButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            System.out.println("Refreshing user table data...");
            tableModel.setRowCount(0);
            Map<String, User> currentUsers = system.getUsers();
            System.out.println("Found " + currentUsers.size() + " users");

            for (Map.Entry<String, User> entry : currentUsers.entrySet()) {
                User u = entry.getValue();
                tableModel.addRow(new Object[]{
                        u.getUsername(),
                        u.getRole(),
                        u.getPassword()
                });
            }

            tableModel.fireTableDataChanged();
            userTable.revalidate();
            userTable.repaint();
            System.out.println("User table refreshed");
        }

        private void showUserDialog(User user) {
            JDialog dialog = new JDialog();
            dialog.setTitle(user == null ? "Add User" : "Edit User");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField usernameField = new JTextField();
            JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
            JPasswordField passwordField = new JPasswordField();

            if (user != null) {
                usernameField.setText(user.getUsername());
                roleCombo.setSelectedItem(user.getRole());
                usernameField.setEditable(false); // Username shouldn't be changed
            }

            dialog.add(new JLabel("Username:"));
            dialog.add(usernameField);
            dialog.add(new JLabel("Role:"));
            dialog.add(roleCombo);
            dialog.add(new JLabel("Password:"));
            dialog.add(passwordField);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    UserRole role = (UserRole) roleCombo.getSelectedItem();

                    if (username.isEmpty() || password.isEmpty()) {
                        throw new IllegalArgumentException("All fields required");
                    }

                    User newUser = new User(username, password, role);
                    System.out.println("Adding/updating user: " + username + " with role: " + role);
                    system.getUsers().put(username, newUser);
                    refreshData();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JLabel());
            dialog.add(buttonPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        private void deleteUser() {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = (String) tableModel.getValueAt(row, 0);
            try {
                System.out.println("Deleting user: " + username);
                system.deleteUser(username);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void resetPassword() {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = (String) tableModel.getValueAt(row, 0);
            String newPassword = JOptionPane.showInputDialog(this, "Enter new password for " + username);

            if (newPassword != null && !newPassword.isEmpty()) {
                System.out.println("Resetting password for user: " + username);
                system.updateUserPassword(username, newPassword);
                refreshData();
            }
        }
    }
}
