package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.mongodb.client.model.Filters.*;

public class UserDAO {
    private MongoCollection<Document> collection;

    public UserDAO() {
        this.collection = DatabaseConnection.getInstance()
                .getDatabase()
                .getCollection("users");
    }

    // Create
    public boolean insertUser(User user) {
        try {
            // Check if user already exists
            if (getUserByUsername(user.getUsername()) != null) {
                System.err.println("User with username " + user.getUsername() + " already exists");
                return false;
            }

            Document doc = new Document("username", user.getUsername())
                    .append("password", user.getPassword())
                    .append("role", user.getRole().toString());

            collection.insertOne(doc);
            System.out.println("User " + user.getUsername() + " inserted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // Read
    public User getUserByUsername(String username) {
        try {
            Document doc = collection.find(eq("username", username)).first();
            if (doc != null) {
                return documentToUser(doc);
            }
        } catch (Exception e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    public Map<String, User> getAllUsers() {
        Map<String, User> users = new HashMap<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                User user = documentToUser(cursor.next());
                users.put(user.getUsername(), user);
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    // Update - FIXED to check if record exists
    public boolean updateUser(User user) {
        try {
            // First check if user exists
            if (getUserByUsername(user.getUsername()) == null) {
                System.err.println("Cannot update: User with username " + user.getUsername() + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document()
                    .append("password", user.getPassword())
                    .append("role", user.getRole().toString()));

            UpdateResult result = collection.updateOne(eq("username", user.getUsername()), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("User " + user.getUsername() + " updated successfully");
                return true;
            } else {
                System.err.println("No user found with username: " + user.getUsername());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Update password - FIXED to check if record exists
    public boolean updateUserPassword(String username, String newPassword) {
        try {
            // First check if user exists
            if (getUserByUsername(username) == null) {
                System.err.println("Cannot update password: User with username " + username + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document("password", newPassword));
            UpdateResult result = collection.updateOne(eq("username", username), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Password updated for user " + username);
                return true;
            } else {
                System.err.println("No user found with username: " + username);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating user password: " + e.getMessage());
            return false;
        }
    }

    // Delete - FIXED to check if record exists
    public boolean deleteUser(String username) {
        try {
            // First check if user exists
            if (getUserByUsername(username) == null) {
                System.err.println("Cannot delete: User with username " + username + " does not exist");
                return false;
            }

            DeleteResult result = collection.deleteOne(eq("username", username));

            if (result.getDeletedCount() > 0) {
                System.out.println("User " + username + " deleted successfully");
                return true;
            } else {
                System.err.println("No user found with username: " + username);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Authentication
    public User authenticateUser(String username, String password) {
        try {
            Document doc = collection.find(and(eq("username", username), eq("password", password))).first();
            if (doc != null) {
                return documentToUser(doc);
            }
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    // Check if user exists
    public boolean userExists(String username) {
        return getUserByUsername(username) != null;
    }

    // Helper method
    private User documentToUser(Document doc) {
        return new User(
                doc.getString("username"),
                doc.getString("password"),
                UserRole.valueOf(doc.getString("role"))
        );
    }

    // Initialize default users if collection is empty
    public void initializeDefaultUsers() {
        try {
            if (collection.countDocuments() == 0) {
                insertUser(new User("admin", "admin123", UserRole.ADMIN));
                insertUser(new User("doctor1", "doc123", UserRole.DOCTOR));
                insertUser(new User("reception1", "recep123", UserRole.RECEPTIONIST));
                System.out.println("Default users initialized in database.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing default users: " + e.getMessage());
        }
    }
}
