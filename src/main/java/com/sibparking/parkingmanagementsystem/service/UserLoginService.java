package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.UserLogin;
import com.sibparking.parkingmanagementsystem.model.UserSession;
import com.sibparking.parkingmanagementsystem.repository.UserLoginRepository;
import com.sibparking.parkingmanagementsystem.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

@Service
public class UserLoginService {

    @Autowired
    private UserLoginRepository userRepository;
    //new changes
    @Autowired
    private UserSessionRepository sessionRepository;

    private static final long SESSION_DURATION = 30 * 60 * 1000;//30 min expiry

    // Save new user
public UserLogin addUser(UserLogin user) {
    return userRepository.save(user);
}
    // Validate user + create session
    public String loginUser(String username, String password, String deviceId, boolean force) {
    UserLogin user = userRepository.findByUsername(username);

    if (user == null) {
        throw new RuntimeException("User not registered.");
    }

    if (!user.getPassword().equals(password)) {
        throw new RuntimeException("Invalid password.");
    }

    // check existing session and force login logic...
    Optional<UserSession> existing = sessionRepository.findByUsernameAndActiveTrue(username);

    if (existing.isPresent()) {
        UserSession session = existing.get();

        if (!session.getDeviceId().equals(deviceId) && session.getExpiryTime().after(new Date())) {
            if (!force) {
                throw new RuntimeException("User already logged in on another device.");
            } else {
                // deactivate old session
                session.setActive(false);
                sessionRepository.save(session);
            }
        } else if (session.getDeviceId().equals(deviceId)) {
            // same device, token lost → deactivate old session
            session.setActive(false);
            sessionRepository.save(session);
        }
    }

        // create new session
        UserSession session = new UserSession();
        session.setUsername(username);
        session.setDeviceId(deviceId);   // save deviceId
        session.setToken(UUID.randomUUID().toString());
        session.setLoginTime(new Date());
        session.setExpiryTime(new Date(System.currentTimeMillis() + SESSION_DURATION));
        session.setActive(true);
        sessionRepository.save(session);

        return session.getToken();

}


    // Validate session
    public boolean validateSession(String token) {
        Optional<UserSession> sessionOpt = sessionRepository.findByTokenAndActiveTrue(token);

        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            if (session.getExpiryTime().after(new Date())) {
                return true;
            } else {
                // expired → deactivate
                session.setActive(false);
                sessionRepository.save(session);
            }
        }
        return false;
    }

    // Logout
    public void logout(String token) {
        Optional<UserSession> sessionOpt = sessionRepository.findByToken(token); // remove active filter
    if (sessionOpt.isPresent()) {
        UserSession session = sessionOpt.get();
        session.setActive(false); // deactivate
        session.setExpiryTime(new Date()); // mark expired
        sessionRepository.save(session);
    }
}
}

    

