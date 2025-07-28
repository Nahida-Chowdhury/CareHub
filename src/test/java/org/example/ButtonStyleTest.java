package org.example;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ButtonStyleTest {

    @Test
    void testRedButtonStyle() {
        JButton button = ButtonStyle.createRedButton("Delete");

        assertEquals("Delete", button.getText());
        assertEquals(new Color(220, 60, 60), button.getBackground());
        assertEquals(Color.WHITE, button.getForeground());
        assertEquals(new Font("Arial", Font.BOLD, 12), button.getFont());
        assertTrue(button.isOpaque());
        assertFalse(button.isFocusPainted());
        assertFalse(button.isBorderPainted());
        assertTrue(button.isContentAreaFilled());

        // Optional: Verify listeners are attached
        assertTrue(button.getMouseListeners().length > 0);
        assertTrue(button.getActionListeners().length > 0);
    }
}
