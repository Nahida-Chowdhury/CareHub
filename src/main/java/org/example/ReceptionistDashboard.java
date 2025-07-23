package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class ReceptionistDashboard extends JPanel {
    private HospitalManagementSystem system;
    private JTabbedPane tabbedPane;

    public ReceptionistDashboard(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> system.showLoginPanel());
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        add(menuBar, BorderLayout.NORTH);

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

            addButton.addActionListener(e -> showAppointmentDialog());
            cancelButton.addActionListener(e -> cancelAppointment());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(refreshButton);
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

            JComboBox<Patient> patientCombo = new JComboBox<>();
            JComboBox<Doctor> doctorCombo = new JComboBox<>();
            JTextField dateField = new JTextField();
            JTextField timeField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);

            // Populate combos
            for (Patient p : system.getAllPatients()) {
                patientCombo.addItem(p);
            }
            for (Doctor d : system.getAllDoctors()) {
                doctorCombo.addItem(d);
            }

            dialog.add(new JLabel("Patient:"));
            dialog.add(patientCombo);
            dialog.add(new JLabel("Doctor:"));
            dialog.add(doctorCombo);
            dialog.add(new JLabel("Date (YYYY-MM-DD):"));
            dialog.add(dateField);
            dialog.add(new JLabel("Time (HH:MM):"));
            dialog.add(timeField);
            dialog.add(new JLabel("Description:"));
            dialog.add(new JScrollPane(descArea));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Patient p = (Patient) patientCombo.getSelectedItem();
                    Doctor d = (Doctor) doctorCombo.getSelectedItem();
                    String date = dateField.getText();
                    String time = timeField.getText();
                    String desc = descArea.getText();

                    if (p == null || d == null || date.isEmpty() || time.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    String id = "APP" + System.currentTimeMillis();
                    system.addAppointment(new Appointment(id, p.getPatientId(), d.getDoctorId(), date, time, desc));
                    refreshData();
                    dialog.dispose();
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

            addButton.addActionListener(e -> showBillDialog());
            payButton.addActionListener(e -> markBillPaid());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(payButton);
            buttonPanel.add(refreshButton);
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

                    String id = "BILL" + System.currentTimeMillis();
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