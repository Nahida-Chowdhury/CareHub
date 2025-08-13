package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class LoginPanel extends JPanel {
    private HospitalManagementSystem system;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

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
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        loginForm.add(loginButton, gbc);

        // Setup Enter key functionality
        setupEnterKeyNavigation();

        // Set initial focus to username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());

        add(loginForm);
    }

    private void setupEnterKeyNavigation() {
        // Username field - Enter moves to password field
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocusInWindow();
                }
            }
        });

        // Password field - Enter triggers login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Login button - Enter triggers login
        loginButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Alternative approach using InputMap/ActionMap for more robust handling
        setupInputMaps();
    }

    private void setupInputMaps() {
        // Setup input maps for Enter key handling
        InputMap usernameInputMap = usernameField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap usernameActionMap = usernameField.getActionMap();

        usernameInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "moveToPassword");
        usernameActionMap.put("moveToPassword", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                passwordField.requestFocusInWindow();
            }
        });

        InputMap passwordInputMap = passwordField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap passwordActionMap = passwordField.getActionMap();

        passwordInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "performLogin");
        passwordActionMap.put("performLogin", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                performLogin();
            }
        });

        // Make login button the default button when it has focus
        InputMap buttonInputMap = loginButton.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap buttonActionMap = loginButton.getActionMap();

        buttonInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "performLogin");
        buttonActionMap.put("performLogin", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
        if (username.isEmpty()) {
            usernameField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            passwordField.requestFocusInWindow();
            return;
        }

        // Disable login button temporarily to prevent multiple clicks
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform authentication in a separate thread to avoid blocking UI
        SwingUtilities.invokeLater(() -> {
            try {
                User user = system.authenticateUser(username, password);
                if (user != null) {
                    // Clear fields on successful login
                    clearFields();

                    // Navigate to appropriate dashboard
                    switch (user.getRole()) {
                        case ADMIN:
                            system.showAdminDashboard();
                            break;
                        case DOCTOR:
                            system.showDoctorDashboard();
                            break;
                        case RECEPTIONIST:
                            system.showReceptionistDashboard();
                            break;
                    }
                } else {
                    // Clear password field and focus on username for retry
                    passwordField.setText("");
                    usernameField.selectAll();
                    usernameField.requestFocusInWindow();
                }
            } finally {
                // Re-enable login button
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        });
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        // Set focus back to username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    // Method to handle focus when panel becomes visible
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
        }
    }

    // Override to ensure proper focus when panel is added
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }
}
