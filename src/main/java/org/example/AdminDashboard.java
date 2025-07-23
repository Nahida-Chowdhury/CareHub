package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Admin Dashboard
class AdminDashboard extends JPanel {
    private HospitalManagementSystem system;
    private JTabbedPane tabbedPane;

    public AdminDashboard(HospitalManagementSystem system) {
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
        JLabel titleLabel = new JLabel("Administrator Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.PAGE_START);

        // Tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patients", new PatientManagementPanel());
        tabbedPane.addTab("Doctors", new DoctorManagementPanel());
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
            patientTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(patientTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Patient");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton refreshButton = new JButton("Refresh");

            addButton.addActionListener(e -> showPatientDialog(null));
            editButton.addActionListener(e -> editPatient());
            deleteButton.addActionListener(e -> deletePatient());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            tableModel.setRowCount(0);
            for (Patient p : system.getAllPatients()) {
                tableModel.addRow(new Object[]{
                        p.getPatientId(), p.getName(), p.getAge(), p.getGender(), p.getPhone()
                });
            }
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
                        system.addPatient(p);
                    } else {
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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete patient " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
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
            doctorTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(doctorTable);
            add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Doctor");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton refreshButton = new JButton("Refresh");

            addButton.addActionListener(e -> showDoctorDialog(null));
            editButton.addActionListener(e -> editDoctor());
            deleteButton.addActionListener(e -> deleteDoctor());
            refreshButton.addActionListener(e -> refreshData());

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);

            refreshData();
        }

        @Override
        public void refreshData() {
            tableModel.setRowCount(0);
            for (Doctor d : system.getAllDoctors()) {
                tableModel.addRow(new Object[]{
                        d.getDoctorId(), d.getName(), d.getSpecialization(), d.getAvailability()
                });
            }
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
                    system.addDoctor(d);
                } else {
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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                system.deleteDoctor(id);
                refreshData();
            }
        }
    }
}

