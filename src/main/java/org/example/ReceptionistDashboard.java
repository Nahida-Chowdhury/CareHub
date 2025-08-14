package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

class ReceptionistDashboard extends JPanel {
    private HospitalManagementSystem system;
    private JTabbedPane tabbedPane;

    public ReceptionistDashboard(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Receptionist Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.PAGE_START);

        // Tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font for tabs
        tabbedPane.addTab("Appointments", new AppointmentManagementPanel());
        tabbedPane.addTab("Billing", new BillingManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        System.out.println("Receptionist Dashboard - refreshing all data...");
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof Refreshable) {
                ((Refreshable) comp).refreshData();
            }
        }
    }

    interface Refreshable {
        void refreshData();
    }

    class AppointmentManagementPanel extends JPanel implements Refreshable {
        private DefaultTableModel tableModel;
        private JTable appointmentTable;

        public AppointmentManagementPanel() {
            setLayout(new BorderLayout());

            // Table setup
            String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Description", "Status"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            appointmentTable = system.new StyledTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(appointmentTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("New Appointment");
            JButton cancelButton = new JButton("Cancel");
            JButton refreshButton = new JButton("Refresh");
            JButton logoutButton = ButtonStyle.createRedButton("Logout");

            addButton.addActionListener(e -> showAppointmentDialog());
            cancelButton.addActionListener(e -> cancelAppointment());
            refreshButton.addActionListener(e -> {
                System.out.println("Appointment Refresh button clicked - refreshing appointment data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });
            logoutButton.addActionListener(e -> system.showLoginPanel());

            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            System.out.println("Refreshing appointment table data...");
            tableModel.setRowCount(0);
            List<Appointment> currentAppointments = system.getAllAppointments();
            System.out.println("Found " + currentAppointments.size() + " appointments");

            for (Appointment a : currentAppointments) {
                // Use stored names if available, otherwise lookup
                String patientName = a.getPatientName();
                String doctorName = a.getDoctorName();

                // Fallback to lookup if names not stored
                if (patientName == null || patientName.isEmpty()) {
                    Patient p = system.getPatientById(a.getPatientId());
                    patientName = p != null ? p.getName() : "Unknown Patient";
                }

                if (doctorName == null || doctorName.isEmpty()) {
                    Doctor d = system.getDoctorById(a.getDoctorId());
                    doctorName = d != null ? d.getName() : "Unknown Doctor";
                }

                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        patientName,
                        doctorName,
                        a.getDate(),
                        a.getTime(),
                        a.getDescription(),
                        a.isCompleted() ? "Completed" : "Pending"
                });
            }

            tableModel.fireTableDataChanged();
            appointmentTable.revalidate();
            appointmentTable.repaint();
            System.out.println("Appointment table refreshed with " + currentAppointments.size() + " appointments");
        }

        private void showAppointmentDialog() {
            JDialog dialog = new JDialog();
            dialog.setTitle("New Appointment");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            // 1. Patient Selection Panel (with New Patient button)
            JPanel patientPanel = new JPanel(new BorderLayout());
            JComboBox<Patient> patientCombo = new JComboBox<>();
            JButton newPatientButton = new JButton("New Patient");
            patientPanel.add(patientCombo, BorderLayout.CENTER);
            patientPanel.add(newPatientButton, BorderLayout.EAST);

            // 2. Doctor Selection
            JComboBox<Doctor> doctorCombo = new JComboBox<>();

            // 3. Appointment Details
            JTextField dateField = new JTextField();
            JTextField timeField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);

            // Initialize comboboxes
            refreshPatientCombo(patientCombo);
            refreshDoctorCombo(doctorCombo);

            // Add components to dialog
            dialog.add(new JLabel("Patient:"));
            dialog.add(patientPanel);
            dialog.add(new JLabel("Doctor:"));
            dialog.add(doctorCombo);
            dialog.add(new JLabel("Date (YYYY-MM-DD):"));
            dialog.add(dateField);
            dialog.add(new JLabel("Time (HH:MM):"));
            dialog.add(timeField);
            dialog.add(new JLabel("Description:"));
            dialog.add(new JScrollPane(descArea));

            // New Patient Button Action
            newPatientButton.addActionListener(e -> {
                JDialog patientDialog = new JDialog(dialog, "Add New Patient", true);
                patientDialog.setLayout(new GridLayout(0, 2, 10, 10));

                // Patient Form Fields
                JTextField nameField = new JTextField();
                JTextField ageField = new JTextField();
                JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
                JTextField phoneField = new JTextField();
                JTextField addressField = new JTextField();

                // Add fields to dialog
                patientDialog.add(new JLabel("Name:"));
                patientDialog.add(nameField);
                patientDialog.add(new JLabel("Age:"));
                patientDialog.add(ageField);
                patientDialog.add(new JLabel("Gender:"));
                patientDialog.add(genderCombo);
                patientDialog.add(new JLabel("Phone:"));
                patientDialog.add(phoneField);
                patientDialog.add(new JLabel("Address:"));
                patientDialog.add(addressField);

                // Save Button for Patient
                JButton savePatientButton = new JButton("Save");
                savePatientButton.addActionListener(ev -> {
                    try {
                        // Validate required fields
                        if (nameField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty() ||
                                phoneField.getText().trim().isEmpty()) {
                            throw new IllegalArgumentException("Please fill all required fields (Name, Age, Phone)");
                        }

                        // Create new patient
                        String id = IDGenerator.generatePatientID();
                        Patient newPatient = new Patient(
                                id,
                                nameField.getText().trim(),
                                Integer.parseInt(ageField.getText().trim()),
                                genderCombo.getSelectedItem().toString(),
                                addressField.getText().trim(),
                                phoneField.getText().trim()
                        );

                        System.out.println("Creating new patient: " + newPatient.getName());
                        system.addPatient(newPatient);
                        refreshPatientCombo(patientCombo);
                        patientCombo.setSelectedItem(newPatient);
                        patientDialog.dispose();
                        JOptionPane.showMessageDialog(dialog, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(patientDialog, "Please enter a valid age (numbers only)", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(patientDialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(patientDialog, "Error creating patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                // Cancel Button for Patient
                JButton cancelPatientButton = new JButton("Cancel");
                cancelPatientButton.addActionListener(ev -> patientDialog.dispose());

                // Button Panel for Patient Dialog
                JPanel patientButtonPanel = new JPanel();
                patientButtonPanel.add(savePatientButton);
                patientButtonPanel.add(cancelPatientButton);

                patientDialog.add(new JLabel());
                patientDialog.add(patientButtonPanel);
                patientDialog.pack();
                patientDialog.setLocationRelativeTo(dialog);
                patientDialog.setVisible(true);
            });

            // Save Button for Appointment
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient p = (Patient) patientCombo.getSelectedItem();
                    Doctor d = (Doctor) doctorCombo.getSelectedItem();
                    String date = dateField.getText().trim();
                    String time = timeField.getText().trim();
                    String desc = descArea.getText().trim();

                    // Validate all fields
                    if (p == null || d == null || date.isEmpty() || time.isEmpty() || desc.isEmpty()) {
                        throw new IllegalArgumentException("Please fill all appointment fields");
                    }

                    // Create new appointment with patient and doctor names
                    String id = IDGenerator.generateAppointmentID();
                    Appointment newAppointment = new Appointment(id, p.getPatientId(), d.getDoctorId(), date, time, desc);
                    // Set the names for better display
                    newAppointment.setNames(p.getName(), d.getName());

                    System.out.println("Creating appointment for " + p.getName() + " with " + d.getName());
                    system.addAppointment(newAppointment);
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Appointment created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Cancel Button for Appointment
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            // Button Panel for Appointment Dialog
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JLabel());
            dialog.add(buttonPanel);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }

        // Helper method to refresh patient combo box
        private void refreshPatientCombo(JComboBox<Patient> combo) {
            DefaultComboBoxModel<Patient> model = (DefaultComboBoxModel<Patient>) combo.getModel();
            model.removeAllElements();
            for (Patient p : system.getAllPatients()) {
                model.addElement(p);
            }
        }

        // Helper method to refresh doctor combo box
        private void refreshDoctorCombo(JComboBox<Doctor> combo) {
            DefaultComboBoxModel<Doctor> model = (DefaultComboBoxModel<Doctor>) combo.getModel();
            model.removeAllElements();
            for (Doctor d : system.getAllDoctors()) {
                model.addElement(d);
            }
        }

        private void cancelAppointment() {
            int row = appointmentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to cancel", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String patientName = (String) tableModel.getValueAt(row, 1);
            String doctorName = (String) tableModel.getValueAt(row, 2);
            String date = (String) tableModel.getValueAt(row, 3);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel appointment?\n\nPatient: " + patientName + "\nDoctor: " + doctorName + "\nDate: " + date,
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Cancelling appointment: " + id);
                system.getAllAppointments().removeIf(a -> a.getAppointmentId().equals(id));
                refreshData();
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JButton refreshButton = new JButton("Refresh");
            JButton logoutButton = ButtonStyle.createRedButton("Logout");

            addButton.addActionListener(e -> showBillDialog());
            payButton.addActionListener(e -> markBillPaid());
            refreshButton.addActionListener(e -> {
                System.out.println("Billing Refresh button clicked - refreshing billing data...");
                system.refreshAllData(); // Refresh from database first
                refreshData(); // Then refresh UI
            });
            logoutButton.addActionListener(e -> system.showLoginPanel());

            buttonPanel.add(addButton);
            buttonPanel.add(payButton);
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
                        p != null ? p.getName() : "Unknown Patient",
                        String.format("$%.2f", b.getAmount()),
                        b.getDescription(),
                        b.isPaid() ? "Paid" : "Pending"
                });
            }

            tableModel.fireTableDataChanged();
            billTable.revalidate();
            billTable.repaint();
            System.out.println("Billing table refreshed with " + currentBills.size() + " bills");
        }

        private void showBillDialog() {
            JDialog dialog = new JDialog();
            dialog.setTitle("New Bill");
            dialog.setModal(true);
            dialog.setLayout(new GridLayout(0, 2, 10, 10));

            JComboBox<Patient> patientCombo = new JComboBox<>();
            JTextField amountField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);

            // Populate patient combo
            for (Patient p : system.getAllPatients()) {
                patientCombo.addItem(p);
            }

            dialog.add(new JLabel("Patient:"));
            dialog.add(patientCombo);
            dialog.add(new JLabel("Amount ($):"));
            dialog.add(amountField);
            dialog.add(new JLabel("Description:"));
            dialog.add(new JScrollPane(descArea));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient p = (Patient) patientCombo.getSelectedItem();
                    String amountText = amountField.getText().trim();
                    String desc = descArea.getText().trim();

                    if (p == null || amountText.isEmpty() || desc.isEmpty()) {
                        throw new IllegalArgumentException("Please fill all fields");
                    }

                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be greater than 0");
                    }

                    String id = IDGenerator.generateBillID();
                    Bill newBill = new Bill(id, p.getPatientId(), amount, desc);

                    System.out.println("Creating bill for " + p.getName() + ": $" + amount);
                    system.addBill(newBill);
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Bill created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid amount (numbers only)", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error creating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Please select a bill to mark as paid", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String patientName = (String) tableModel.getValueAt(row, 1);
            String amount = (String) tableModel.getValueAt(row, 2);
            String status = (String) tableModel.getValueAt(row, 4);

            if ("Paid".equals(status)) {
                JOptionPane.showMessageDialog(this, "This bill is already marked as paid", "Already Paid", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mark bill as paid?\n\nPatient: " + patientName + "\nAmount: " + amount,
                    "Confirm Payment", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Marking bill as paid: " + id);
                system.markBillPaid(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Bill marked as paid successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
