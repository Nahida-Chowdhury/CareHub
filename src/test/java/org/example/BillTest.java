package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillTest {

    private Bill bill;

    @BeforeEach
    void setUp() {
        // Initialize a default bill before each test
        bill = new Bill("B001", "P001", 150.00, "Consultation Fee");
    }

    @Test
    void testBillInitializationAndGetters() {
        assertNotNull(bill, "Bill object should not be null after initialization");
        assertEquals("B001", bill.getBillId(), "Bill ID should match the initialized value");
        assertEquals("P001", bill.getPatientId(), "Patient ID should match the initialized value");
        assertEquals(150.00, bill.getAmount(), 0.001, "Amount should match the initialized value");
        assertEquals("Consultation Fee", bill.getDescription(), "Description should match the initialized value");
        assertFalse(bill.isPaid(), "Bill should be unpaid by default");
    }

    @Test
    void testSetPaid() {
        bill.setPaid(true);
        assertTrue(bill.isPaid(), "Bill should be marked as paid");

        bill.setPaid(false);
        assertFalse(bill.isPaid(), "Bill should be marked as unpaid");
    }

    @Test
    void testBillWithNullStringValues() {
        Bill nullBill = new Bill("B002", null, 200.00, null);
        assertNotNull(nullBill, "Bill object should be created even with null strings");
        assertEquals("B002", nullBill.getBillId());
        assertNull(nullBill.getPatientId(), "Patient ID should be null");
        assertEquals(200.00, nullBill.getAmount(), 0.001);
        assertNull(nullBill.getDescription(), "Description should be null");
        assertFalse(nullBill.isPaid());
    }

    @Test
    void testBillWithEmptyStringValues() {
        Bill emptyBill = new Bill("B003", "", 50.00, "");
        assertNotNull(emptyBill, "Bill object should be created even with empty strings");
        assertEquals("B003", emptyBill.getBillId());
        assertTrue(emptyBill.getPatientId().isEmpty(), "Patient ID should be empty");
        assertEquals(50.00, emptyBill.getAmount(), 0.001);
        assertTrue(emptyBill.getDescription().isEmpty(), "Description should be empty");
        assertFalse(emptyBill.isPaid());
    }

    @ParameterizedTest
    @CsvSource({
            "B004, P002, 75.50, X-Ray",
            "B005, P003, 0.00, Free Checkup", // Edge case for amount: 0
            "B006, P004, 1000.00, Surgery Cost"
    })
    void testBillInitializationWithCsvSource(String billId, String patientId, double amount, String description) {
        Bill b = new Bill(billId, patientId, amount, description);

        assertNotNull(b);
        assertEquals(billId, b.getBillId());
        assertEquals(patientId, b.getPatientId());
        assertEquals(amount, b.getAmount(), 0.001);
        assertEquals(description, b.getDescription());
        assertFalse(b.isPaid()); // Should always be unpaid on creation
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSetPaidParameterized(boolean paidStatus) {
        bill.setPaid(paidStatus);
        assertEquals(paidStatus, bill.isPaid(), "Bill paid status should match parameterized value");
    }

    @Test
    void testBillConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> new Bill("B007", "P005", 250.75, "Dental Procedure"),
                "Bill constructor should not throw an exception for valid inputs");
    }
}