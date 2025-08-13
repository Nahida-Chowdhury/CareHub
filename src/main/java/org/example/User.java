package org.example;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.io.Serializable;

public class User implements Serializable {
    private ObjectId id;

    @BsonProperty("username")
    private String username;

    @BsonProperty("password")
    private String password;

    @BsonProperty("role")
    private UserRole role;

    public User() {} // Required no-arg constructor for MongoDB

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}