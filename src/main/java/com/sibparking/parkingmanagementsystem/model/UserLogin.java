package com.sibparking.parkingmanagementsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userlogin")  // MongoDB collection name
public class UserLogin {

    @Id
    private String id;
    private String username;
    private String password;  // For simplicity, plain text (but normally should be hashed)   // e.g., ADMIN, MANAGER, GUARD, USER

    // Constructors
    public UserLogin() {}

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
