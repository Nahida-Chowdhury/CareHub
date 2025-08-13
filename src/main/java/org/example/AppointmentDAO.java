package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class AppointmentDAO {
    private MongoCollection<Document> collection;

    public AppointmentDAO() {
        this.collection = DatabaseConnection.getInstance()
                .getDatabase()
                .getCollection("appointments");
    }

    // Create
    public boolean insertAppointment(Appointment appointment) {
        try {
            // Check if appointment already exists
            if (getAppointmentById(appointment.getAppointmentId()) != null) {
                System.err.println("Appointment with ID " + appointment.getAppointmentId() + " already exists");
                return false;
            }

            Document doc = new Document("appointmentId", appointment.getAppointmentId())
                    .append("patientId", appointment.getPatientId())
                    .append("patientName", appointment.getPatientName()) // Added patient name
                    .append("doctorId", appointment.getDoctorId())
                    .append("doctorName", appointment.getDoctorName()) // Added doctor name
                    .append("date", appointment.getDate())
                    .append("time", appointment.getTime())
                    .append("description", appointment.getDescription())
                    .append("completed", appointment.isCompleted());

            collection.insertOne(doc);
            System.out.println("Appointment " + appointment.getAppointmentId() + " inserted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting appointment: " + e.getMessage());
            return false;
        }
    }

    // Read
    public Appointment getAppointmentById(String appointmentId) {
        try {
            Document doc = collection.find(eq("appointmentId", appointmentId)).first();
            if (doc != null) {
                return documentToAppointment(doc);
            }
        } catch (Exception e) {
            System.err.println("Error getting appointment: " + e.getMessage());
        }
        return null;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                appointments.add(documentToAppointment(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting all appointments: " + e.getMessage());
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatientId(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("patientId", patientId)).iterator();
            while (cursor.hasNext()) {
                appointments.add(documentToAppointment(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting appointments by patient: " + e.getMessage());
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("doctorId", doctorId)).iterator();
            while (cursor.hasNext()) {
                appointments.add(documentToAppointment(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting appointments by doctor: " + e.getMessage());
        }
        return appointments;
    }

    // Update - FIXED to check if record exists
    public boolean updateAppointment(Appointment appointment) {
        try {
            // First check if appointment exists
            if (getAppointmentById(appointment.getAppointmentId()) == null) {
                System.err.println("Cannot update: Appointment with ID " + appointment.getAppointmentId() + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document()
                    .append("patientId", appointment.getPatientId())
                    .append("patientName", appointment.getPatientName()) // Update patient name
                    .append("doctorId", appointment.getDoctorId())
                    .append("doctorName", appointment.getDoctorName()) // Update doctor name
                    .append("date", appointment.getDate())
                    .append("time", appointment.getTime())
                    .append("description", appointment.getDescription())
                    .append("completed", appointment.isCompleted()));

            UpdateResult result = collection.updateOne(eq("appointmentId", appointment.getAppointmentId()), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Appointment " + appointment.getAppointmentId() + " updated successfully");
                return true;
            } else {
                System.err.println("No appointment found with ID: " + appointment.getAppointmentId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    // Mark appointment completed - FIXED to check if record exists
    public boolean markAppointmentCompleted(String appointmentId) {
        try {
            // First check if appointment exists
            if (getAppointmentById(appointmentId) == null) {
                System.err.println("Cannot complete: Appointment with ID " + appointmentId + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document("completed", true));
            UpdateResult result = collection.updateOne(eq("appointmentId", appointmentId), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Appointment " + appointmentId + " marked as completed");
                return true;
            } else {
                System.err.println("No appointment found with ID: " + appointmentId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error marking appointment completed: " + e.getMessage());
            return false;
        }
    }

    // Delete - FIXED to check if record exists
    public boolean deleteAppointment(String appointmentId) {
        try {
            // First check if appointment exists
            if (getAppointmentById(appointmentId) == null) {
                System.err.println("Cannot delete: Appointment with ID " + appointmentId + " does not exist");
                return false;
            }

            DeleteResult result = collection.deleteOne(eq("appointmentId", appointmentId));

            if (result.getDeletedCount() > 0) {
                System.out.println("Appointment " + appointmentId + " deleted successfully");
                return true;
            } else {
                System.err.println("No appointment found with ID: " + appointmentId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    // Check if appointment exists
    public boolean appointmentExists(String appointmentId) {
        return getAppointmentById(appointmentId) != null;
    }

    // Helper method - Updated to handle patient and doctor names
    private Appointment documentToAppointment(Document doc) {
        Appointment appointment = new Appointment(
                doc.getString("appointmentId"),
                doc.getString("patientId"),
                doc.getString("doctorId"),
                doc.getString("date"),
                doc.getString("time"),
                doc.getString("description")
        );

        // Set completion status
        appointment.setCompleted(doc.getBoolean("completed", false));

        // Set patient and doctor names if available
        String patientName = doc.getString("patientName");
        String doctorName = doc.getString("doctorName");

        if (patientName != null) {
            appointment.setPatientName(patientName);
        }
        if (doctorName != null) {
            appointment.setDoctorName(doctorName);
        }

        return appointment;
    }
}
