package org.example;

import java.io.Serializable;

class User implements Serializable {
    private String username, password;
    private UserRole role;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
}
