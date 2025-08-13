package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class DoctorDAO {
    private MongoCollection<Document> collection;

    public DoctorDAO() {
        this.collection = DatabaseConnection.getInstance()
                .getDatabase()
                .getCollection("doctors");
    }

    // Create
    public boolean insertDoctor(Doctor doctor) {
        try {
            // Check if doctor already exists
            if (getDoctorById(doctor.getDoctorId()) != null) {
                System.err.println("Doctor with ID " + doctor.getDoctorId() + " already exists");
                return false;
            }

            Document doc = new Document("doctorId", doctor.getDoctorId())
                    .append("name", doctor.getName())
                    .append("specialization", doctor.getSpecialization())
                    .append("availability", doctor.getAvailability());

            collection.insertOne(doc);
            System.out.println("Doctor " + doctor.getDoctorId() + " inserted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting doctor: " + e.getMessage());
            return false;
        }
    }

    // Read
    public Doctor getDoctorById(String doctorId) {
        try {
            Document doc = collection.find(eq("doctorId", doctorId)).first();
            if (doc != null) {
                return documentToDoctor(doc);
            }
        } catch (Exception e) {
            System.err.println("Error getting doctor: " + e.getMessage());
        }
        return null;
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                doctors.add(documentToDoctor(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting all doctors: " + e.getMessage());
        }
        return doctors;
    }

    // Update - FIXED to check if record exists
    public boolean updateDoctor(Doctor doctor) {
        try {
            // First check if doctor exists
            if (getDoctorById(doctor.getDoctorId()) == null) {
                System.err.println("Cannot update: Doctor with ID " + doctor.getDoctorId() + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document()
                    .append("name", doctor.getName())
                    .append("specialization", doctor.getSpecialization())
                    .append("availability", doctor.getAvailability()));

            UpdateResult result = collection.updateOne(eq("doctorId", doctor.getDoctorId()), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Doctor " + doctor.getDoctorId() + " updated successfully");
                return true;
            } else {
                System.err.println("No doctor found with ID: " + doctor.getDoctorId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            return false;
        }
    }

    // Delete - FIXED to check if record exists
    public boolean deleteDoctor(String doctorId) {
        try {
            // First check if doctor exists
            if (getDoctorById(doctorId) == null) {
                System.err.println("Cannot delete: Doctor with ID " + doctorId + " does not exist");
                return false;
            }

            DeleteResult result = collection.deleteOne(eq("doctorId", doctorId));

            if (result.getDeletedCount() > 0) {
                System.out.println("Doctor " + doctorId + " deleted successfully");
                return true;
            } else {
                System.err.println("No doctor found with ID: " + doctorId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            return false;
        }
    }

    // Check if doctor exists
    public boolean doctorExists(String doctorId) {
        return getDoctorById(doctorId) != null;
    }

    // Helper method
    private Doctor documentToDoctor(Document doc) {
        return new Doctor(
                doc.getString("doctorId"),
                doc.getString("name"),
                doc.getString("specialization"),
                doc.getString("availability")
        );
    }
}
