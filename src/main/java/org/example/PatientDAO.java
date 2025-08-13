package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class PatientDAO {
    private MongoCollection<Document> collection;

    public PatientDAO() {
        this.collection = DatabaseConnection.getInstance()
                .getDatabase()
                .getCollection("patients");
    }

    // Create
    public boolean insertPatient(Patient patient) {
        try {
            // Check if patient already exists
            if (getPatientById(patient.getPatientId()) != null) {
                System.err.println("Patient with ID " + patient.getPatientId() + " already exists");
                return false;
            }

            Document doc = new Document("patientId", patient.getPatientId())
                    .append("name", patient.getName())
                    .append("age", patient.getAge())
                    .append("gender", patient.getGender())
                    .append("address", patient.getAddress())
                    .append("phone", patient.getPhone())
                    .append("allergies", patient.getAllergies())
                    .append("medications", convertMedicationsToDocuments(patient.getMedications()))
                    .append("medicalHistory", convertMedicalRecordsToDocuments(patient.getMedicalHistory()));

            collection.insertOne(doc);
            System.out.println("Patient " + patient.getPatientId() + " inserted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting patient: " + e.getMessage());
            return false;
        }
    }

    // Read
    public Patient getPatientById(String patientId) {
        try {
            Document doc = collection.find(eq("patientId", patientId)).first();
            if (doc != null) {
                return documentToPatient(doc);
            }
        } catch (Exception e) {
            System.err.println("Error getting patient: " + e.getMessage());
        }
        return null;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                patients.add(documentToPatient(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting all patients: " + e.getMessage());
        }
        return patients;
    }

    // Update - FIXED to check if record exists
    public boolean updatePatient(Patient patient) {
        try {
            System.out.println("=== DAO UPDATE PATIENT DEBUG ===");
            System.out.println("Attempting to update patient: " + patient.getPatientId());

            // First check if patient exists
            Patient existingPatient = getPatientById(patient.getPatientId());
            System.out.println("Patient exists in database: " + (existingPatient != null));

            if (existingPatient == null) {
                System.err.println("Cannot update: Patient with ID " + patient.getPatientId() + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document()
                    .append("name", patient.getName())
                    .append("age", patient.getAge())
                    .append("gender", patient.getGender())
                    .append("address", patient.getAddress())
                    .append("phone", patient.getPhone())
                    .append("allergies", patient.getAllergies())
                    .append("medications", convertMedicationsToDocuments(patient.getMedications()))
                    .append("medicalHistory", convertMedicalRecordsToDocuments(patient.getMedicalHistory())));

            System.out.println("Executing MongoDB update...");
            UpdateResult result = collection.updateOne(eq("patientId", patient.getPatientId()), updateDoc);

            System.out.println("MongoDB update result - Matched: " + result.getMatchedCount() + ", Modified: " + result.getModifiedCount());

            if (result.getMatchedCount() > 0) {
                System.out.println("Patient " + patient.getPatientId() + " updated successfully");
                return true;
            } else {
                System.err.println("No patient found with ID: " + patient.getPatientId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete - FIXED to check if record exists
    public boolean deletePatient(String patientId) {
        try {
            // First check if patient exists
            if (getPatientById(patientId) == null) {
                System.err.println("Cannot delete: Patient with ID " + patientId + " does not exist");
                return false;
            }

            DeleteResult result = collection.deleteOne(eq("patientId", patientId));

            if (result.getDeletedCount() > 0) {
                System.out.println("Patient " + patientId + " deleted successfully");
                return true;
            } else {
                System.err.println("No patient found with ID: " + patientId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            return false;
        }
    }

    // Delete all patients
    public boolean deleteAllPatients() {
        try {
            DeleteResult result = collection.deleteMany(new Document()); // Empty filter = delete all
            System.out.println("Deleted " + result.getDeletedCount() + " patients");
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting all patients: " + e.getMessage());
            return false;
        }
    }

    // Get count of patients
    public long getPatientCount() {
        try {
            return collection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error counting patients: " + e.getMessage());
            return 0;
        }
    }

    // Check if patient exists
    public boolean patientExists(String patientId) {
        return getPatientById(patientId) != null;
    }

    // Helper methods
    private Patient documentToPatient(Document doc) {
        Patient patient = new Patient(
                doc.getString("patientId"),
                doc.getString("name"),
                doc.getInteger("age", 0),
                doc.getString("gender"),
                doc.getString("address"),
                doc.getString("phone")
        );

        // Add allergies
        List<String> allergies = doc.getList("allergies", String.class);
        if (allergies != null) {
            for (String allergy : allergies) {
                patient.addAllergy(allergy);
            }
        }

        // Add medications
        List<Document> medicationDocs = doc.getList("medications", Document.class);
        if (medicationDocs != null) {
            for (Document medDoc : medicationDocs) {
                Medication medication = new Medication(
                        medDoc.getString("name"),
                        medDoc.getString("dosage"),
                        medDoc.getString("frequency"),
                        medDoc.getString("startDate"),
                        medDoc.getString("endDate")
                );
                patient.addMedication(medication);
            }
        }

        // Add medical history
        List<Document> historyDocs = doc.getList("medicalHistory", Document.class);
        if (historyDocs != null) {
            for (Document histDoc : historyDocs) {
                MedicalRecord record = new MedicalRecord(
                        histDoc.getString("recordId"),
                        histDoc.getString("patientId"),
                        histDoc.getString("doctorId"),
                        histDoc.getString("diagnosis"),
                        histDoc.getString("treatment"),
                        histDoc.getString("notes")
                );
                patient.addMedicalRecord(record);
            }
        }

        return patient;
    }

    private List<Document> convertMedicationsToDocuments(List<Medication> medications) {
        List<Document> docs = new ArrayList<>();
        for (Medication med : medications) {
            docs.add(new Document()
                    .append("name", med.getName())
                    .append("dosage", med.getDosage())
                    .append("frequency", med.getFrequency())
                    .append("startDate", med.getStartDate())
                    .append("endDate", med.getEndDate())
                    .append("isActive", med.isActive()));
        }
        return docs;
    }

    private List<Document> convertMedicalRecordsToDocuments(List<MedicalRecord> records) {
        List<Document> docs = new ArrayList<>();
        for (MedicalRecord record : records) {
            docs.add(new Document()
                    .append("recordId", record.getRecordId())
                    .append("patientId", record.getPatientId())
                    .append("doctorId", record.getDoctorId())
                    .append("diagnosis", record.getDiagnosis())
                    .append("treatment", record.getTreatment())
                    .append("notes", record.getNotes())
                    .append("visitDate", record.getVisitDate())
                    .append("visitTime", record.getVisitTime()));
        }
        return docs;
    }
}
