package com.future.accountservice.service;

import com.future.accountservice.entity.Role;
import com.future.accountservice.entity.User;
import com.future.accountservice.exception.AccountAPIException;
import com.future.accountservice.payload.JwtAuthResponseDTO;
import com.future.accountservice.payload.LoginRequestDTO;
import com.future.accountservice.payload.UserRegistrationDTO;
import com.future.accountservice.repository.RoleRepository;
import com.future.accountservice.repository.UserRepository;
import com.future.futurecommon.security.JwtTokenProvider;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role role;
    private LoginRequestDTO loginRequestDTO;
    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Collections.singleton(role));

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsernameOrEmail("testuser");
        loginRequestDTO.setPassword("password123");

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setEmail("newuser@example.com");
        registrationDTO.setPassword("newpassword");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");
    }

    @Test
    void login_ShouldReturnJwtResponse_WhenCredentialsAreValid() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyString(), any(Map.class)))
                .thenReturn("test-token");

        // Act
        JwtAuthResponseDTO response = authService.login(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("test-token", response.getAccessToken());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameOrEmail(anyString(), anyString());
        verify(jwtTokenProvider).generateToken(anyString(), any(Map.class));
    }

    @Test
    void register_ShouldCreateUser_WhenDetailsAreValid() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(snowflakeIdGenerator.generateId()).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = authService.register(registrationDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository).existsByUsername(registrationDTO.getUsername());
        verify(userRepository).existsByEmail(registrationDTO.getEmail());
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode(registrationDTO.getPassword());
        verify(snowflakeIdGenerator).generateId();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        AccountAPIException exception = assertThrows(AccountAPIException.class, () -> {
            authService.register(registrationDTO);
        });
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).existsByUsername(registrationDTO.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        AccountAPIException exception = assertThrows(AccountAPIException.class, () -> {
            authService.register(registrationDTO);
        });
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByUsername(registrationDTO.getUsername());
        verify(userRepository).existsByEmail(registrationDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}