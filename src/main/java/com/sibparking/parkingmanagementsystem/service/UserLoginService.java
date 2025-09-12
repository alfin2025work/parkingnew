package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.UserLogin;
import com.sibparking.parkingmanagementsystem.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    @Autowired
    private UserLoginRepository userRepository;

    // Save new user
public UserLogin addUser(UserLogin user) {
    return userRepository.save(user);
}
    // Validate login
    public boolean validateUser(String username, String password) {
        UserLogin user = userRepository.findByUsername(username);
        return (user != null && user.getPassword().equals(password));
    }
}

