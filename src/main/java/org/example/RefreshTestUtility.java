package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshTestUtility extends JFrame {
    private PatientDAO patientDAO;
    private JTextArea logArea;

    public RefreshTestUtility() {
        setTitle("Refresh Test Utility");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        patientDAO = new PatientDAO();

        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Database Refresh Test", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addPatientButton = new JButton("Add Test Patient");
        JButton refreshButton = new JButton("Refresh Data");
        JButton countButton = new JButton("Count Patients");
        JButton clearLogButton = new JButton("Clear Log");

        addPatientButton.addActionListener(e -> addTestPatient());
        refreshButton.addActionListener(e -> refreshAndDisplay());
        countButton.addActionListener(e -> countPatients());
        clearLogButton.addActionListener(e -> logArea.setText(""));

        buttonPanel.add(addPatientButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(countButton);
        buttonPanel.add(clearLogButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial display
        refreshAndDisplay();
    }

    private void addTestPatient() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String patientId = "TEST" + timestamp.substring(timestamp.length() - 6);

            Patient testPatient = new Patient(
                    patientId,
                    "Test Patient " + timestamp.substring(timestamp.length() - 4),
                    25,
                    "Male",
                    "123 Test Street",
                    "555-TEST"
            );

            log("Adding test patient: " + patientId);
            boolean success = patientDAO.insertPatient(testPatient);

            if (success) {
                log("✓ Patient added successfully!");
                log("Now click 'Refresh Data' to see the new patient in the list");
            } else {
                log("✗ Failed to add patient");
            }

        } catch (Exception e) {
            log("Error adding patient: " + e.getMessage());
        }
    }

    private void refreshAndDisplay() {
        try {
            log("Refreshing patient data from database...");

            java.util.List<Patient> patients = patientDAO.getAllPatients();

            log("Found " + patients.size() + " patients:");
            log("----------------------------------------");

            for (Patient p : patients) {
                log(p.getPatientId() + " - " + p.getName() + " (Age: " + p.getAge() + ")");
            }

            log("----------------------------------------");
            log("Refresh completed at: " + new java.util.Date());

        } catch (Exception e) {
            log("Error refreshing data: " + e.getMessage());
        }
    }

    private void countPatients() {
        try {
            long count = patientDAO.getPatientCount();
            log("Total patients in database: " + count);
        } catch (Exception e) {
            log("Error counting patients: " + e.getMessage());
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new RefreshTestUtility().setVisible(true);
        });
    }
}
