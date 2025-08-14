package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class DoctorDashboard extends JPanel {
    private HospitalManagementSystem system;
    private DefaultTableModel appointmentTableModel;
    private DefaultTableModel medicalRecordsTableModel;
    private JTable appointmentTable;
    private JTable medicalRecordsTable;
    private JTabbedPane tabbedPane;

    public DoctorDashboard(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Doctor Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.PAGE_START);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font for tabs
        // Appointments tab
        JPanel appointmentsPanel = createAppointmentsPanel();
        tabbedPane.addTab("Appointments", appointmentsPanel);

        // Medical Records tab
        JPanel medicalRecordsPanel = createMedicalRecordsPanel();
        tabbedPane.addTab("Medical Records", medicalRecordsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton logoutButton = ButtonStyle.createRedButton("Logout");
        logoutButton.addActionListener(e -> system.showLoginPanel());
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Appointments table
        String[] columns = {"ID", "Patient", "Date", "Time", "Description", "Status"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        appointmentTable = system.new StyledTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel for appointments
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("Mark Completed");
        JButton refreshButton = new JButton("Refresh");
        JButton addRecordButton = new JButton("Add Medical Record");

        completeButton.addActionListener(e -> markAppointmentCompleted());
        refreshButton.addActionListener(e -> {
            System.out.println("Refresh button clicked - refreshing doctor dashboard data...");
            system.refreshAllData(); // Refresh from database first
            refreshData(); // Then refresh UI
        });
        addRecordButton.addActionListener(e -> showAddMedicalRecordDialog());

        buttonPanel.add(completeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addRecordButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMedicalRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Record ID", "Patient", "Date", "Diagnosis", "Treatment"};
        medicalRecordsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        medicalRecordsTable = system.new StyledTable(medicalRecordsTableModel);
        JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel for medical records
        JPanel buttonPanel = new JPanel();
        JButton viewDetailsButton = new JButton("View Details");
        JButton manageMedicationsButton = new JButton("Manage Medications");
        JButton manageAllergiesButton = new JButton("Manage Allergies");

        viewDetailsButton.addActionListener(e -> viewMedicalRecordDetails());
        manageMedicationsButton.addActionListener(e -> showMedicationDialog());
        manageAllergiesButton.addActionListener(e -> showAllergyDialog());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(manageMedicationsButton);
        buttonPanel.add(manageAllergiesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void refreshData() {
        System.out.println("Refreshing doctor dashboard data...");

        // Refresh appointments
        appointmentTableModel.setRowCount(0);
        List<Appointment> currentAppointments = system.getAllAppointments();
        System.out.println("Found " + currentAppointments.size() + " appointments");

        for (Appointment a : currentAppointments) {
            Patient p = system.getPatientById(a.getPatientId());
            String patientName = p != null ? p.getName() : "Unknown";
            appointmentTableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    patientName,
                    a.getDate(),
                    a.getTime(),
                    a.getDescription(),
                    a.isCompleted() ? "Completed" : "Pending"
            });
        }

        // Refresh medical records
        medicalRecordsTableModel.setRowCount(0);
        List<Patient> currentPatients = system.getAllPatients();
        System.out.println("Processing medical records for " + currentPatients.size() + " patients");

        for (Patient patient : currentPatients) {
            for (MedicalRecord record : patient.getMedicalHistory()) {
                medicalRecordsTableModel.addRow(new Object[]{
                        record.getRecordId(),
                        patient.getName(),
                        record.getVisitDate(),
                        record.getDiagnosis(),
                        record.getTreatment()
                });
            }
        }

        // Refresh table displays
        appointmentTable.revalidate();
        appointmentTable.repaint();
        medicalRecordsTable.revalidate();
        medicalRecordsTable.repaint();

        System.out.println("Doctor dashboard refreshed");
    }

    private void markAppointmentCompleted() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) appointmentTableModel.getValueAt(row, 0);
        system.markAppointmentCompleted(id);
        refreshData();
    }

    private void showAddMedicalRecordDialog() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) appointmentTableModel.getValueAt(row, 0);
        Appointment appointment = system.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().equals(appointmentId))
                .findFirst().orElse(null);

        if (appointment == null) return;

        Patient patient = system.getPatientById(appointment.getPatientId());
        if (patient == null) return;

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Medical Record", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Patient info
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Patient: " + patient.getName()), gbc);

        // Diagnosis
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        JTextField diagnosisField = new JTextField(20);
        dialog.add(diagnosisField, gbc);

        // Treatment
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Treatment:"), gbc);
        gbc.gridx = 1;
        JTextField treatmentField = new JTextField(20);
        dialog.add(treatmentField, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        JTextArea notesArea = new JTextArea(3, 20);
        dialog.add(new JScrollPane(notesArea), gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String recordId = "MR" + System.currentTimeMillis();
            MedicalRecord record = new MedicalRecord(
                    recordId,
                    patient.getPatientId(),
                    "DOC1", // Current doctor - could be made dynamic
                    diagnosisField.getText(),
                    treatmentField.getText(),
                    notesArea.getText()
            );

            patient.addMedicalRecord(record);
            refreshData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Medical record added successfully!");
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewMedicalRecordDetails() {
        int row = medicalRecordsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medical record", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordId = (String) medicalRecordsTableModel.getValueAt(row, 0);

        // Find the record
        MedicalRecord record = null;
        Patient patient = null;
        for (Patient p : system.getAllPatients()) {
            for (MedicalRecord r : p.getMedicalHistory()) {
                if (r.getRecordId().equals(recordId)) {
                    record = r;
                    patient = p;
                    break;
                }
            }
            if (record != null) break;
        }

        if (record == null) return;

        // Show details dialog
        String details = String.format(
                "Patient: %s\nDate: %s %s\nDiagnosis: %s\nTreatment: %s\nNotes: %s\n\nAllergies: %s\nCurrent Medications: %s",
                patient.getName(),
                record.getVisitDate(),
                record.getVisitTime(),
                record.getDiagnosis(),
                record.getTreatment(),
                record.getNotes(),
                String.join(", ", patient.getAllergies()),
                patient.getMedications().toString()
        );

        JOptionPane.showMessageDialog(this, details, "Medical Record Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMedicationDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        Patient patient = system.getPatientById(patientId);
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Patient not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Medications - " + patient.getName(), true);
        dialog.setLayout(new BorderLayout());

        // Current medications list
        DefaultListModel<Medication> listModel = new DefaultListModel<>();
        for (Medication med : patient.getMedications()) {
            listModel.addElement(med);
        }
        JList<Medication> medicationList = new JList<>(listModel);
        dialog.add(new JScrollPane(medicationList), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Medication");
        JButton removeButton = new JButton("Remove Selected");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(dialog, "Medication Name:");
            if (name != null && !name.trim().isEmpty()) {
                String dosage = JOptionPane.showInputDialog(dialog, "Dosage:");
                String frequency = JOptionPane.showInputDialog(dialog, "Frequency:");
                String startDate = JOptionPane.showInputDialog(dialog, "Start Date (YYYY-MM-DD):");
                String endDate = JOptionPane.showInputDialog(dialog, "End Date (YYYY-MM-DD):");

                Medication medication = new Medication(name, dosage, frequency, startDate, endDate);
                patient.addMedication(medication);
                listModel.addElement(medication);
            }
        });

        removeButton.addActionListener(e -> {
            Medication selected = medicationList.getSelectedValue();
            if (selected != null) {
                patient.removeMedication(selected.getName());
                listModel.removeElement(selected);
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAllergyDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        Patient patient = system.getPatientById(patientId);
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Patient not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Allergies - " + patient.getName(), true);
        dialog.setLayout(new BorderLayout());

        // Current allergies list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String allergy : patient.getAllergies()) {
            listModel.addElement(allergy);
        }
        JList<String> allergyList = new JList<>(listModel);
        dialog.add(new JScrollPane(allergyList), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Allergy");
        JButton removeButton = new JButton("Remove Selected");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            String allergy = JOptionPane.showInputDialog(dialog, "Enter Allergy:");
            if (allergy != null && !allergy.trim().isEmpty()) {
                patient.addAllergy(allergy);
                listModel.addElement(allergy);
            }
        });

        removeButton.addActionListener(e -> {
            String selected = allergyList.getSelectedValue();
            if (selected != null) {
                patient.removeAllergy(selected);
                listModel.removeElement(selected);
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
