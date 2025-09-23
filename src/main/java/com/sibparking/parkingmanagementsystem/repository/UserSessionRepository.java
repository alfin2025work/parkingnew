package com.sibparking.parkingmanagementsystem.repository;

import com.sibparking.parkingmanagementsystem.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    Optional<UserSession> findByUsernameAndActiveTrue(String username);
    Optional<UserSession> findByTokenAndActiveTrue(String token);
}