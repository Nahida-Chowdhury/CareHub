package org.example;

public class IDGenerator {
    private static int patientCounter = 3;
    private static int doctorCounter = 3;
    private static int appointmentCounter = 2;
    private static int billCounter = 2;

    public static String generatePatientID() {
        return "PAT" + (++patientCounter);
    }

    public static String generateDoctorID() {
        return "DOC" + (++doctorCounter);
    }

    public static String generateAppointmentID() {
        return "APP" + (++appointmentCounter);
    }

    public static String generateBillID() {
        return "BILL" + (++billCounter);
    }

    // Initialize counters from existing data
    public static void initializeCounters(HospitalManagementSystem system) {
        patientCounter = system.getAllPatients().size();
        doctorCounter = system.getAllDoctors().size();
        appointmentCounter = system.getAllAppointments().size();
        billCounter = system.getAllBills().size();
    }
}