package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginPanelTest {

    private HospitalManagementSystem system;
    private LoginPanel loginPanel;

    @BeforeEach
    void setUp() {
        system = new HospitalManagementSystem();
        loginPanel = system.getLoginPanel();
    }

    @Test
    void testClearFields() {
        JTextField usernameField = getUsernameField();
        JPasswordField passwordField = getPasswordField();

        usernameField.setText("admin");
        passwordField.setText("admin123");

        loginPanel.clearFields();

        assertEquals("", usernameField.getText());
        assertEquals("", new String(passwordField.getPassword()));
    }

    @Test
    void testSuccessfulLoginWithAdmin() {
        JTextField usernameField = getUsernameField();
        JPasswordField passwordField = getPasswordField();
        JButton loginButton = getLoginButton();

        usernameField.setText("admin");
        passwordField.setText("admin123");

        loginButton.doClick(); // Triggers login logic

        User user = system.authenticateUser("admin", "admin123");
        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testFailedLogin() {
        JTextField usernameField = getUsernameField();
        JPasswordField passwordField = getPasswordField();
        JButton loginButton = getLoginButton();

        usernameField.setText("wrong");
        passwordField.setText("user");

        loginButton.doClick();

        User user = system.authenticateUser("wrong", "user");
        assertNull(user);
    }

    // ------- UI Component Extractors -------
    private JTextField getUsernameField() {
        return (JTextField) findComponent(loginPanel, JTextField.class);
    }

    private JPasswordField getPasswordField() {
        return (JPasswordField) findComponent(loginPanel, JPasswordField.class);
    }

    private JButton getLoginButton() {
        return (JButton) findComponent(loginPanel, JButton.class);
    }

    private Component findComponent(Container container, Class<?> clazz) {
        for (Component comp : container.getComponents()) {
            if (clazz.isInstance(comp)) return comp;
            if (comp instanceof Container nested) {
                Component result = findComponent(nested, clazz);
                if (result != null) return result;
            }
        }
        return null;
    }
}
