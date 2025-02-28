package com.future.accountservice.controller;

import com.future.accountservice.entity.User;
import com.future.accountservice.payload.JwtAuthResponseDTO;
import com.future.accountservice.payload.LoginRequestDTO;
import com.future.accountservice.payload.UserDTO;
import com.future.accountservice.payload.UserRegistrationDTO;
import com.future.accountservice.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account-service/auth")
public class AuthController {

    private final AuthService authService;
    private final ModelMapper modelMapper;

    public AuthController(AuthService authService, ModelMapper modelMapper) {
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        JwtAuthResponseDTO jwtAuthResponseDTO = authService.login(loginRequestDTO);
        return new ResponseEntity<>(jwtAuthResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO registrationDTO) {
        User registeredUser = authService.register(registrationDTO);
        UserDTO userDTO = modelMapper.map(registeredUser, UserDTO.class);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }
}