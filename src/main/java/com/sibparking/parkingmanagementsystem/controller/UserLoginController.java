package com.sibparking.parkingmanagementsystem.controller;

import com.sibparking.parkingmanagementsystem.model.UserLogin;
import com.sibparking.parkingmanagementsystem.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserLoginController {

    @Autowired
    private UserLoginService userService;


    // Login
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String deviceId,
                               @RequestParam(required = false, defaultValue = "false") boolean force) {
    Map<String, Object> response = new HashMap<>();
    try {
        String token = userService.loginUser(username, password, deviceId,force);
        response.put("status", "success");
        response.put("token", token);
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        response.put("status", e.getMessage());
        return ResponseEntity.status(403).body(response);
    }
}

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
         if (token.startsWith("Bearer ")) {
        token = token.substring(7); // remove "Bearer "
    }
    userService.logout(token); // <-- actually deactivate the session
        Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Logged out successfully.");

    return ResponseEntity.ok(response);
    }

    // Validate session
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    boolean valid = userService.validateSession(token);
        Map<String, Object> response = new HashMap<>();
    if (valid) {
        response.put("status", "success");
        response.put("message", "Session is valid");
    } else {
        response.put("status", "failed");
        response.put("message", "Session expired or invalid");
    }

    return ResponseEntity.ok(response);
}

}



