package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class MedicationTest {

    private Medication medication;

    @BeforeEach
    void setUp() {
        medication = new Medication("Lisinopril", "10mg", "Once daily", "2025-08-01", "2025-12-01");
    }

    @ParameterizedTest
    @CsvSource({
            "Lisinopril, 10mg, Once daily, 2025-08-01, 2025-12-01",
            "Metformin, 500mg, Twice daily, 2025-01-01, 2025-06-01",
            "Aspirin, 81mg, Once daily, 2025-02-01, 2025-08-01"
    })
    @DisplayName("Should create medications with various valid inputs")
    void testMedicationCreationWithMultipleInputs(String name, String dosage, String frequency,
                                                  String startDate, String endDate) {
        Medication med = new Medication(name, dosage, frequency, startDate, endDate);

        assertNotNull(med);
        assertEquals(name, med.getName());
        assertEquals(dosage, med.getDosage());
        assertEquals(frequency, med.getFrequency());
        assertEquals(startDate, med.getStartDate());
        assertEquals(endDate, med.getEndDate());
        assertTrue(med.isActive());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should handle null and empty strings")
    void testNullAndEmptyStrings(String input) {
        Medication med = new Medication(input, input, input, input, input);

        assertEquals(input, med.getName());
        assertEquals(input, med.getDosage());
        assertEquals(input, med.getFrequency());
        assertTrue(med.isActive());
    }

    @ParameterizedTest
    @CsvSource({
            "Lisinopril, 10mg, Once daily, 'Lisinopril - 10mg (Once daily)'",
            "'', '', '', ' -  ()'"
    })
    @DisplayName("Should format toString correctly")
    void testToStringWithVariousInputs(String name, String dosage, String frequency, String expected) {
        Medication med = new Medication(name, dosage, frequency, "2025-01-01", "2025-06-01");
        String result = med.toString();

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Should handle active status changes correctly")
    void testActiveStatusChanges(boolean activeStatus) {
        Medication med = new Medication("TestMed", "5mg", "Daily", "2025-01-01", "2025-06-01");

        assertTrue(med.isActive());
        med.setActive(activeStatus);
        assertEquals(activeStatus, med.isActive());
    }

    @Test
    @DisplayName("Should be serializable")
    void testSerializable() {
        assertTrue(medication instanceof java.io.Serializable);
    }
}
