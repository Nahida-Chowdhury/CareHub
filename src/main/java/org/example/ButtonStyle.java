package org.example;

import javax.swing.*;
import java.awt.*;

public class ButtonStyle {
    public static JButton createRedButton(String text) {
        JButton button = new JButton(text);

        // Base styling
        button.setBackground(new Color(220, 60, 60));  // Vibrant red
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 100, 100));  // Darker red
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 60, 60));  // Original red
            }
        });

        // Click effect
        button.addActionListener(e -> {
            button.setBackground(new Color(180, 150, 150));  // Darkest red
            Timer timer = new Timer(200, ev -> {
                button.setBackground(new Color(220, 60, 60));
            });
            timer.setRepeats(false);
            timer.start();
        });

        return button;
    }
}
