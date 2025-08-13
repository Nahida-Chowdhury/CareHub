package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private static final String CONNECTION_STRING = "mongodb+srv://CareHub:CareHub@carehub.77fpx0m.mongodb.net/" +
            "?retryWrites=true&w=majority&appName=CareHub";
    private static final String DATABASE_NAME = "CareHub";

    private DatabaseConnection() {
        try {
            ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);

            System.out.println("Connected to MongoDB Atlas successfully!");
            System.out.println("Database: " + DATABASE_NAME);
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB Atlas: " + e.getMessage());
            System.err.println("Please check your connection string and network connectivity.");
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }

    // Test connection method
    public boolean testConnection() {
        try {
            if (database == null) {
                return false;
            }
            database.runCommand(new org.bson.Document("ping", 1));
            return true;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Get connection info
    public String getConnectionInfo() {
        if (database != null) {
            return "Connected to: " + database.getName();
        }
        return "Not connected";
    }
}
