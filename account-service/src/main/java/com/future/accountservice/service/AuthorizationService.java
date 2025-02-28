package com.future.accountservice.service;

import com.future.accountservice.entity.User;
import com.future.accountservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if a user is authorized
     *
     * @param userId The user ID to check
     * @return true if the user exists and is active, false otherwise
     */
    public boolean isUserAuthorized(Long userId) {
        return userRepository.findById(userId)
                .map(User::isActive)
                .orElse(false);
    }
}