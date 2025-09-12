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


    // Login existing user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user) {
        boolean isValid = userService.validateUser(user.getUsername(), user.getPassword());

        Map<String, Object> response = new HashMap<>();

        if (isValid) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }
}


