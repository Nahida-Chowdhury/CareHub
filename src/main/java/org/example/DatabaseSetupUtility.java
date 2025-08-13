package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseSetupUtility extends JFrame {
    private DatabaseSchemaInitializer schemaInitializer;
    private JTextArea logArea;
    private JButton initializeButton;
    private JButton dropButton;
    private JButton statsButton;
    private JButton testConnectionButton;

    public DatabaseSetupUtility() {
        setTitle("MongoDB Database Setup Utility");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Redirect System.out to the log area
        redirectSystemOut();
    }

    private void initializeComponents() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);

        testConnectionButton = new JButton("Test Connection");
        initializeButton = new JButton("Initialize Schema & Data");
        dropButton = new JButton("Drop All Collections");
        statsButton = new JButton("Show Statistics");

        // Style buttons
        styleButton(testConnectionButton, new Color(0, 123, 255));
        styleButton(initializeButton, new Color(40, 167, 69));
        styleButton(dropButton, new Color(220, 53, 69));
        styleButton(statsButton, new Color(108, 117, 125));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("MongoDB Database Setup Utility", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Log area
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Setup Log"));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(testConnectionButton);
        buttonPanel.add(initializeButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(dropButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        testConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testConnection();
            }
        });

        initializeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeSchema();
            }
        });

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dropCollections();
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatistics();
            }
        });
    }

    private void testConnection() {
        SwingUtilities.invokeLater(() -> {
            logArea.append("Testing database connection...\n");

            try {
                boolean connected = DatabaseConnection.getInstance().testConnection();
                if (connected) {
                    logArea.append("✓ Database connection successful!\n");
                    logArea.append("Database: " + DatabaseConnection.getInstance().getDatabase().getName() + "\n");
                } else {
                    logArea.append("✗ Database connection failed!\n");
                }
            } catch (Exception ex) {
                logArea.append("✗ Connection error: " + ex.getMessage() + "\n");
            }

            logArea.append("----------------------------------------\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void initializeSchema() {
        SwingUtilities.invokeLater(() -> {
            try {
                logArea.append("Starting schema initialization...\n");
                schemaInitializer = new DatabaseSchemaInitializer();
                schemaInitializer.initializeSchema();
                logArea.append("✓ Schema initialization completed!\n");
            } catch (Exception ex) {
                logArea.append("✗ Schema initialization failed: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }

            logArea.append("----------------------------------------\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void dropCollections() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to drop all collections?\nThis will delete ALL data!",
                "Confirm Drop Collections",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                try {
                    logArea.append("Dropping all collections...\n");
                    if (schemaInitializer == null) {
                        schemaInitializer = new DatabaseSchemaInitializer();
                    }
                    schemaInitializer.dropAllCollections();
                    logArea.append("✓ All collections dropped successfully!\n");
                } catch (Exception ex) {
                    logArea.append("✗ Error dropping collections: " + ex.getMessage() + "\n");
                }

                logArea.append("----------------------------------------\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    private void showStatistics() {
        SwingUtilities.invokeLater(() -> {
            try {
                logArea.append("Fetching database statistics...\n");
                if (schemaInitializer == null) {
                    schemaInitializer = new DatabaseSchemaInitializer();
                }
                schemaInitializer.printCollectionStats();
            } catch (Exception ex) {
                logArea.append("✗ Error getting statistics: " + ex.getMessage() + "\n");
            }

            logArea.append("----------------------------------------\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void redirectSystemOut() {
        // Create a custom PrintStream that writes to the JTextArea
        java.io.PrintStream originalOut = System.out;
        java.io.PrintStream customOut = new java.io.PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(() -> {
                    logArea.append(String.valueOf((char) b));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        });

        System.setOut(customOut);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new DatabaseSetupUtility().setVisible(true);
        });
    }
}
