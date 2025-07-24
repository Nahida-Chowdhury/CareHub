package org.example;

import org.example.IDGenerator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class ReceptionistDashboard extends JPanel {
    private HospitalManagementSystem system;
    private JTabbedPane tabbedPane;

    public ReceptionistDashboard(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Receptionist Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.PAGE_START);

        // Tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Appointments", new AppointmentManagementPanel());
        tabbedPane.addTab("Billing", new BillingManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshData() {
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
            appointmentTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(appointmentTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("New Appointment");
            JButton cancelButton = new JButton("Cancel");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showAppointmentDialog());
            cancelButton.addActionListener(e -> cancelAppointment());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            tableModel.setRowCount(0);
            for (Appointment a : system.getAllAppointments()) {
                Patient p = system.getPatientById(a.getPatientId());
                Doctor d = system.getDoctorById(a.getDoctorId());
                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        p != null ? p.getName() : "Unknown",
                        d != null ? d.getName() : "Unknown",
                        a.getDate(),
                        a.getTime(),
                        a.getDescription(),
                        a.isCompleted() ? "Completed" : "Pending"
                });
            }
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
            for (Doctor d : system.getAllDoctors()) {
                doctorCombo.addItem(d);
            }

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
                        if (nameField.getText().isEmpty() || ageField.getText().isEmpty() ||
                                phoneField.getText().isEmpty()) {
                            throw new IllegalArgumentException("Please fill all required fields");
                        }

                        // Create new patient
                        String id = IDGenerator.generatePatientID();
                        Patient newPatient = new Patient(
                                id,
                                nameField.getText(),
                                Integer.parseInt(ageField.getText()),
                                genderCombo.getSelectedItem().toString(),
                                addressField.getText(),
                                phoneField.getText()
                        );

                        system.addPatient(newPatient);
                        refreshPatientCombo(patientCombo);
                        patientCombo.setSelectedItem(newPatient);
                        patientDialog.dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(patientDialog, "Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(patientDialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    String date = dateField.getText();
                    String time = timeField.getText();
                    String desc = descArea.getText();

                    // Validate all fields
                    if (p == null || d == null || date.isEmpty() || time.isEmpty() || desc.isEmpty()) {
                        throw new IllegalArgumentException("Please fill all appointment fields");
                    }

                    // Create new appointment
                    String id = IDGenerator.generateAppointmentID();
                    system.addAppointment(new Appointment(id, p.getPatientId(), d.getDoctorId(), date, time, desc));
                    refreshData();
                    dialog.dispose();

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

        private void cancelAppointment() {
            int row = appointmentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel appointment " + id + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                system.getAllAppointments().removeIf(a -> a.getAppointmentId().equals(id));
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
            billTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(billTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("New Bill");
            JButton payButton = new JButton("Mark Paid");
            JButton refreshButton = new JButton("Refresh");
            // Styled Logout Button
            JButton logoutButton = ButtonStyle.createRedButton("Logout");
            logoutButton.addActionListener(e -> system.showLoginPanel());

            addButton.addActionListener(e -> showBillDialog());
            payButton.addActionListener(e -> markBillPaid());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(payButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(logoutButton);

            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            tableModel.setRowCount(0);
            for (Bill b : system.getAllBills()) {
                Patient p = system.getPatientById(b.getPatientId());
                tableModel.addRow(new Object[]{
                        b.getBillId(),
                        p != null ? p.getName() : "Unknown",
                        String.format("$%.2f", b.getAmount()),
                        b.getDescription(),
                        b.isPaid() ? "Paid" : "Pending"
                });
            }
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
                    system.addBill(new Bill(id, p.getPatientId(), amount, desc));
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
            system.markBillPaid(id);
            refreshData();
        }
    }
}