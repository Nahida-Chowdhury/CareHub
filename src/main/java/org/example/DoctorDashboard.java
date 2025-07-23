package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class DoctorDashboard extends JPanel {
    private HospitalManagementSystem system;
    private DefaultTableModel tableModel;
    private JTable appointmentTable;

    public DoctorDashboard(HospitalManagementSystem system) {
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
        JLabel titleLabel = new JLabel("Doctor Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.PAGE_START);

        // Appointments table
        String[] columns = {"ID", "Patient", "Date", "Time", "Description", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        appointmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("Mark Completed");
        JButton refreshButton = new JButton("Refresh");

        completeButton.addActionListener(e -> markAppointmentCompleted());
        refreshButton.addActionListener(e -> refreshData());

        buttonPanel.add(completeButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        for (Appointment a : system.getAllAppointments()) {
            Patient p = system.getPatientById(a.getPatientId());
            String patientName = p != null ? p.getName() : "Unknown";
            tableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    patientName,
                    a.getDate(),
                    a.getTime(),
                    a.getDescription(),
                    a.isCompleted() ? "Completed" : "Pending"
            });
        }
    }

    private void markAppointmentCompleted() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        system.markAppointmentCompleted(id);
        refreshData();
    }
}
