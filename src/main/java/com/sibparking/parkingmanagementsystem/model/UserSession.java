package com.sibparking.parkingmanagementsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "user_sessions")
public class UserSession {
    @Id
    private String id;
    private String username;
    private String token;      // random UUID for session
    private Date loginTime;
    private Date expiryTime;   // ⏳ session expiry
    private boolean active;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }

    public Date getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Date expiryTime) { this.expiryTime = expiryTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}