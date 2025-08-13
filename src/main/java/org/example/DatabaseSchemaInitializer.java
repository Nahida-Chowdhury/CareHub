package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Arrays;
import java.util.List;

public class DatabaseSchemaInitializer {
    private MongoDatabase database;

    public DatabaseSchemaInitializer() {
        this.database = DatabaseConnection.getInstance().getDatabase();
    }

    public void initializeSchema() {
        try {
            System.out.println("Initializing MongoDB schema...");

            // Create collections with validation rules
            createUsersCollection();
            createPatientsCollection();
            createDoctorsCollection();
            createAppointmentsCollection();
            createBillsCollection();

            // Create indexes for better performance
            createIndexes();

            // Insert sample data
            insertSampleData();

            System.out.println("MongoDB schema initialized successfully!");

        } catch (Exception e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createUsersCollection() {
        try {
            // Create users collection with validation
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("username", "password", "role"))
                    .append("properties", new Document()
                            .append("username", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Username must be a string and is required"))
                            .append("password", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Password must be a string and is required"))
                            .append("role", new Document()
                                    .append("enum", Arrays.asList("ADMIN", "DOCTOR", "RECEPTIONIST"))
                                    .append("description", "Role must be one of ADMIN, DOCTOR, or RECEPTIONIST"))));

            database.createCollection("users",
                    new com.mongodb.client.model.CreateCollectionOptions()
                            .validationOptions(new com.mongodb.client.model.ValidationOptions().validator(validator)));

            System.out.println("Users collection created with validation rules");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Users collection already exists");
            } else {
                System.err.println("Error creating users collection: " + e.getMessage());
            }
        }
    }

    private void createPatientsCollection() {
        try {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("patientId", "name", "age", "gender", "phone"))
                    .append("properties", new Document()
                            .append("patientId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^PAT[0-9]+$")
                                    .append("description", "Patient ID must be in format PAT followed by numbers"))
                            .append("name", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Name must be a string"))
                            .append("age", new Document()
                                    .append("bsonType", "int")
                                    .append("minimum", 0)
                                    .append("maximum", 150)
                                    .append("description", "Age must be between 0 and 150"))
                            .append("gender", new Document()
                                    .append("enum", Arrays.asList("Male", "Female", "Other"))
                                    .append("description", "Gender must be Male, Female, or Other"))
                            .append("phone", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Phone must be a string"))
                            .append("address", new Document()
                                    .append("bsonType", "string"))
                            .append("allergies", new Document()
                                    .append("bsonType", "array")
                                    .append("items", new Document().append("bsonType", "string")))
                            .append("medications", new Document()
                                    .append("bsonType", "array"))
                            .append("medicalHistory", new Document()
                                    .append("bsonType", "array"))));

            database.createCollection("patients",
                    new com.mongodb.client.model.CreateCollectionOptions()
                            .validationOptions(new com.mongodb.client.model.ValidationOptions().validator(validator)));

            System.out.println("Patients collection created with validation rules");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Patients collection already exists");
            } else {
                System.err.println("Error creating patients collection: " + e.getMessage());
            }
        }
    }

    private void createDoctorsCollection() {
        try {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("doctorId", "name", "specialization", "availability"))
                    .append("properties", new Document()
                            .append("doctorId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^DOC[0-9]+$")
                                    .append("description", "Doctor ID must be in format DOC followed by numbers"))
                            .append("name", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Name must be a string"))
                            .append("specialization", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Specialization must be a string"))
                            .append("availability", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Availability must be a string"))));

            database.createCollection("doctors",
                    new com.mongodb.client.model.CreateCollectionOptions()
                            .validationOptions(new com.mongodb.client.model.ValidationOptions().validator(validator)));

            System.out.println("Doctors collection created with validation rules");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Doctors collection already exists");
            } else {
                System.err.println("Error creating doctors collection: " + e.getMessage());
            }
        }
    }

    private void createAppointmentsCollection() {
        try {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("appointmentId", "patientId", "doctorId", "date", "time", "description"))
                    .append("properties", new Document()
                            .append("appointmentId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^APP[0-9]+$")
                                    .append("description", "Appointment ID must be in format APP followed by numbers"))
                            .append("patientId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^PAT[0-9]+$"))
                            .append("doctorId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^DOC[0-9]+$"))
                            .append("date", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Date in YYYY-MM-DD format"))
                            .append("time", new Document()
                                    .append("bsonType", "string")
                                    .append("description", "Time in HH:MM format"))
                            .append("description", new Document()
                                    .append("bsonType", "string"))
                            .append("completed", new Document()
                                    .append("bsonType", "bool"))));

            database.createCollection("appointments",
                    new com.mongodb.client.model.CreateCollectionOptions()
                            .validationOptions(new com.mongodb.client.model.ValidationOptions().validator(validator)));

            System.out.println("Appointments collection created with validation rules");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Appointments collection already exists");
            } else {
                System.err.println("Error creating appointments collection: " + e.getMessage());
            }
        }
    }

    private void createBillsCollection() {
        try {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", Arrays.asList("billId", "patientId", "amount", "description"))
                    .append("properties", new Document()
                            .append("billId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^BILL[0-9]+$")
                                    .append("description", "Bill ID must be in format BILL followed by numbers"))
                            .append("patientId", new Document()
                                    .append("bsonType", "string")
                                    .append("pattern", "^PAT[0-9]+$"))
                            .append("amount", new Document()
                                    .append("bsonType", "double")
                                    .append("minimum", 0)
                                    .append("description", "Amount must be a positive number"))
                            .append("description", new Document()
                                    .append("bsonType", "string"))
                            .append("paid", new Document()
                                    .append("bsonType", "bool"))));

            database.createCollection("bills",
                    new com.mongodb.client.model.CreateCollectionOptions()
                            .validationOptions(new com.mongodb.client.model.ValidationOptions().validator(validator)));

            System.out.println("Bills collection created with validation rules");
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Bills collection already exists");
            } else {
                System.err.println("Error creating bills collection: " + e.getMessage());
            }
        }
    }

    private void createIndexes() {
        try {
            // Users collection indexes
            MongoCollection<Document> usersCollection = database.getCollection("users");
            usersCollection.createIndex(new Document("username", 1)); // Unique index on username

            // Patients collection indexes
            MongoCollection<Document> patientsCollection = database.getCollection("patients");
            patientsCollection.createIndex(new Document("patientId", 1)); // Unique index on patientId
            patientsCollection.createIndex(new Document("name", 1)); // Index on name for search
            patientsCollection.createIndex(new Document("phone", 1)); // Index on phone

            // Doctors collection indexes
            MongoCollection<Document> doctorsCollection = database.getCollection("doctors");
            doctorsCollection.createIndex(new Document("doctorId", 1)); // Unique index on doctorId
            doctorsCollection.createIndex(new Document("specialization", 1)); // Index on specialization

            // Appointments collection indexes
            MongoCollection<Document> appointmentsCollection = database.getCollection("appointments");
            appointmentsCollection.createIndex(new Document("appointmentId", 1)); // Unique index on appointmentId
            appointmentsCollection.createIndex(new Document("patientId", 1)); // Index on patientId
            appointmentsCollection.createIndex(new Document("doctorId", 1)); // Index on doctorId
            appointmentsCollection.createIndex(new Document("date", 1)); // Index on date
            appointmentsCollection.createIndex(new Document("completed", 1)); // Index on completed status

            // Bills collection indexes
            MongoCollection<Document> billsCollection = database.getCollection("bills");
            billsCollection.createIndex(new Document("billId", 1)); // Unique index on billId
            billsCollection.createIndex(new Document("patientId", 1)); // Index on patientId
            billsCollection.createIndex(new Document("paid", 1)); // Index on paid status

            System.out.println("Database indexes created successfully");
        } catch (Exception e) {
            System.err.println("Error creating indexes: " + e.getMessage());
        }
    }

    private void insertSampleData() {
        try {
            // Check if data already exists
            if (database.getCollection("users").countDocuments() > 0) {
                System.out.println("Sample data already exists, skipping insertion");
                return;
            }

            // Insert sample users
            MongoCollection<Document> usersCollection = database.getCollection("users");
            List<Document> users = Arrays.asList(
                    new Document("username", "admin")
                            .append("password", "admin123")
                            .append("role", "ADMIN"),
                    new Document("username", "doctor1")
                            .append("password", "doc123")
                            .append("role", "DOCTOR"),
                    new Document("username", "reception1")
                            .append("password", "recep123")
                            .append("role", "RECEPTIONIST")
            );
            usersCollection.insertMany(users);

            // Insert sample doctors
            MongoCollection<Document> doctorsCollection = database.getCollection("doctors");
            List<Document> doctors = Arrays.asList(
                    new Document("doctorId", "DOC1")
                            .append("name", "Dr. Smith")
                            .append("specialization", "Cardiology")
                            .append("availability", "9AM-5PM"),
                    new Document("doctorId", "DOC2")
                            .append("name", "Dr. Johnson")
                            .append("specialization", "Neurology")
                            .append("availability", "10AM-6PM"),
                    new Document("doctorId", "DOC3")
                            .append("name", "Dr. Williams")
                            .append("specialization", "Pediatrics")
                            .append("availability", "8AM-4PM")
            );
            doctorsCollection.insertMany(doctors);

            // Insert sample patients
            MongoCollection<Document> patientsCollection = database.getCollection("patients");
            List<Document> patients = Arrays.asList(
                    new Document("patientId", "PAT1")
                            .append("name", "John Doe")
                            .append("age", 35)
                            .append("gender", "Male")
                            .append("address", "123 Main St")
                            .append("phone", "555-1234")
                            .append("allergies", Arrays.asList())
                            .append("medications", Arrays.asList())
                            .append("medicalHistory", Arrays.asList()),
                    new Document("patientId", "PAT2")
                            .append("name", "Jane Smith")
                            .append("age", 28)
                            .append("gender", "Female")
                            .append("address", "456 Oak Ave")
                            .append("phone", "555-5678")
                            .append("allergies", Arrays.asList("Peanuts"))
                            .append("medications", Arrays.asList())
                            .append("medicalHistory", Arrays.asList()),
                    new Document("patientId", "PAT3")
                            .append("name", "Robert Johnson")
                            .append("age", 45)
                            .append("gender", "Male")
                            .append("address", "789 Pine Rd")
                            .append("phone", "555-9012")
                            .append("allergies", Arrays.asList())
                            .append("medications", Arrays.asList())
                            .append("medicalHistory", Arrays.asList())
            );
            patientsCollection.insertMany(patients);

            // Insert sample appointments
            MongoCollection<Document> appointmentsCollection = database.getCollection("appointments");
            List<Document> appointments = Arrays.asList(
                    new Document("appointmentId", "APP1")
                            .append("patientId", "PAT1")
                            .append("doctorId", "DOC1")
                            .append("date", "2024-01-15")
                            .append("time", "10:00")
                            .append("description", "Regular checkup")
                            .append("completed", false),
                    new Document("appointmentId", "APP2")
                            .append("patientId", "PAT2")
                            .append("doctorId", "DOC2")
                            .append("date", "2024-01-15")
                            .append("time", "11:30")
                            .append("description", "Headache consultation")
                            .append("completed", false)
            );
            appointmentsCollection.insertMany(appointments);

            // Insert sample bills
            MongoCollection<Document> billsCollection = database.getCollection("bills");
            List<Document> bills = Arrays.asList(
                    new Document("billId", "BILL1")
                            .append("patientId", "PAT1")
                            .append("amount", 150.00)
                            .append("description", "Consultation fee")
                            .append("paid", false),
                    new Document("billId", "BILL2")
                            .append("patientId", "PAT2")
                            .append("amount", 200.00)
                            .append("description", "Lab tests")
                            .append("paid", false)
            );
            billsCollection.insertMany(bills);

            System.out.println("Sample data inserted successfully");
        } catch (Exception e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }

    public void dropAllCollections() {
        try {
            System.out.println("Dropping all collections...");
            database.getCollection("users").drop();
            database.getCollection("patients").drop();
            database.getCollection("doctors").drop();
            database.getCollection("appointments").drop();
            database.getCollection("bills").drop();
            System.out.println("All collections dropped successfully");
        } catch (Exception e) {
            System.err.println("Error dropping collections: " + e.getMessage());
        }
    }

    public void printCollectionStats() {
        try {
            System.out.println("\n=== Database Statistics ===");

            String[] collections = {"users", "patients", "doctors", "appointments", "bills"};

            for (String collectionName : collections) {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                long count = collection.countDocuments();
                System.out.println(collectionName + ": " + count + " documents");
            }

            System.out.println("============================\n");
        } catch (Exception e) {
            System.err.println("Error getting collection stats: " + e.getMessage());
        }
    }
}
