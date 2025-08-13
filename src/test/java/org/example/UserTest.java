package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User adminUser;
    private User doctorUser;
    private User receptionistUser;

    @BeforeEach
    void setUp() {
        adminUser = new User("admin", "admin123", UserRole.ADMIN);
        doctorUser = new User("doctor1", "doc123", UserRole.DOCTOR);
        receptionistUser = new User("reception1", "recep123", UserRole.RECEPTIONIST);
    }

    @Test
    void testGetUsername() {
        assertEquals("admin", adminUser.getUsername());
        assertEquals("doctor1", doctorUser.getUsername());
        assertEquals("reception1", receptionistUser.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("admin123", adminUser.getPassword());
        assertEquals("doc123", doctorUser.getPassword());
        assertEquals("recep123", receptionistUser.getPassword());
    }

    @Test
    void testGetRole() {
        assertEquals(UserRole.ADMIN, adminUser.getRole());
        assertEquals(UserRole.DOCTOR, doctorUser.getRole());
        assertEquals(UserRole.RECEPTIONIST, receptionistUser.getRole());
    }

    @Test
    void testNotNullFields() {
        assertNotNull(adminUser.getUsername());
        assertNotNull(adminUser.getPassword());
        assertNotNull(adminUser.getRole());
    }

    @Test
    void testNegativeCases() {
        assertNotEquals("wrongUser", adminUser.getUsername());
        assertNotEquals("wrongPass", doctorUser.getPassword());
        assertNotEquals(UserRole.ADMIN, receptionistUser.getRole());
    }
}
