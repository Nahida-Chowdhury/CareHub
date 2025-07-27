package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LoginPanel extends JPanel {
    private HospitalManagementSystem system;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(HospitalManagementSystem system) {
        this.system = system;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel loginForm = new JPanel(new GridBagLayout());
        loginForm.setBorder(BorderFactory.createTitledBorder("Hospital Management System Login"));
        loginForm.setPreferredSize(new Dimension(400, 250));

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        loginForm.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        loginForm.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        loginForm.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        loginForm.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = system.authenticateUser(username, password);
            if (user != null) {
                switch (user.getRole()) {
                    case ADMIN: system.showAdminDashboard(); break;
                    case DOCTOR: system.showDoctorDashboard(); break;
                    case RECEPTIONIST: system.showReceptionistDashboard(); break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginForm.add(loginButton, gbc);

        add(loginForm);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}