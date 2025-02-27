package com.future.accountservice.service;

import com.future.accountservice.entity.Role;
import com.future.accountservice.entity.User;
import com.future.accountservice.repository.RoleRepository;
import com.future.accountservice.repository.UserRepository;
import com.future.futurecommon.security.JwtTokenProvider;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       SnowflakeIdGenerator snowflakeIdGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    public JwtAuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsernameOrEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsernameOrEmail(loginRequestDTO.getUsernameOrEmail(),
                        loginRequestDTO.getUsernameOrEmail())
                .orElseThrow(() -> new AccountAPIException(
                        HttpStatus.BAD_REQUEST, "User not found"));

        // Claims for the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername(), claims);

        JwtAuthResponseDTO jwtAuthResponseDTO = new JwtAuthResponseDTO();
        jwtAuthResponseDTO.setAccessToken(token);
        jwtAuthResponseDTO.setUserId(user.getId());
        jwtAuthResponseDTO.setUsername(user.getUsername());
        jwtAuthResponseDTO.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return jwtAuthResponseDTO;
    }

    public User register(UserRegistrationDTO registrationDTO) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new AccountAPIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new AccountAPIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Create a new user
        User user = new User();
        user.setId(snowflakeIdGenerator.generateId());
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setPhone(registrationDTO.getPhone());
        user.setActive(true);

        // Assign roles
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AccountAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Role USER not found"));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}