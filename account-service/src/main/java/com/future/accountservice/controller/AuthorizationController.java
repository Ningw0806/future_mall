package com.future.accountservice.controller;

import com.future.accountservice.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/account-service/auth")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Checks if a user is authorized
     * This endpoint is intended to be called by other microservices (like Order Service)
     * to verify if a user is authorized to perform operations
     *
     * @param userId The user ID to check
     * @return Boolean flag indicating if the user is authorized
     */
    @GetMapping("/is-authorized/{userId}")
    public ResponseEntity<Map<String, Boolean>> isUserAuthorized(@PathVariable Long userId) {
        boolean isAuthorized = authorizationService.isUserAuthorized(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthorized", isAuthorized);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}