package com.example.demo.service;

import com.example.demo.controller.AuthController.RefreshTokenRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        try {
            // DEBUG: Print incoming request details
            System.out.println("=== REGISTER REQUEST DEBUG ===");
            System.out.println("Request object is null: " + (request == null));
            
            if (request != null) {
                System.out.println("Request toString: " + request.toString());
                System.out.println("Username value: '" + request.getUsername() + "'");
                System.out.println("Username is null: " + (request.getUsername() == null));
                System.out.println("Password is null: " + (request.getPassword() == null));
                System.out.println("ConfirmPassword is null: " + (request.getConfirmPassword() == null));
                System.out.println("Request isValid: " + request.isValid());
                System.out.println("Passwords match: " + request.isPasswordMatching());
                
                if (request.getUsername() != null) {
                    System.out.println("Username length: " + request.getUsername().length());
                    System.out.println("Username after trim: '" + request.getUsername().trim() + "'");
                    System.out.println("Username trim isEmpty: " + request.getUsername().trim().isEmpty());
                }
            }
            System.out.println("===============================");
            
            // Null checks first
            if (request == null) {
                System.out.println("ERROR: Request object is null");
                return new AuthResponse("Invalid request");
            }
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                System.out.println("ERROR: Username validation failed");
                System.out.println("Username is null: " + (request.getUsername() == null));
                if (request.getUsername() != null) {
                    System.out.println("Username trim isEmpty: " + request.getUsername().trim().isEmpty());
                }
                return new AuthResponse("Username is required");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                System.out.println("ERROR: Password validation failed");
                return new AuthResponse("Password is required");
            }
            
            if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
                System.out.println("ERROR: Confirm password validation failed");
                return new AuthResponse("Confirm password is required");
            }
            
            // Check if passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                System.out.println("ERROR: Passwords do not match");
                return new AuthResponse("Passwords do not match");
            }
            
            // Additional password validation
            if (request.getPassword().length() < 6) {
                System.out.println("ERROR: Password too short");
                return new AuthResponse("Password must be at least 6 characters long");
            }
            
            System.out.println("All validations passed. Checking username existence...");
            
            // Check if username exists
            if (userRepository.existsByUsername(request.getUsername().trim())) {
                System.out.println("ERROR: Username already exists");
                return new AuthResponse("Username already exists");
            }
            
            System.out.println("Username is available. Creating user...");
            
            // Create new user
            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            
            System.out.println("Saving user to database...");
            
            // Save user to database
            User savedUser = userRepository.save(user);
            
            System.out.println("User saved successfully. Generating token...");
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(savedUser.getUsername());
            
            System.out.println("Registration successful for user: " + savedUser.getUsername());
            return new AuthResponse(token, savedUser.getUsername());
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Registration error: " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getName());
            e.printStackTrace();
            return new AuthResponse("Registration failed: " + e.getMessage());
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // DEBUG: Print incoming login request details
            System.out.println("=== LOGIN REQUEST DEBUG ===");
            System.out.println("Request object is null: " + (request == null));
            
            if (request != null) {
                System.out.println("Username value: '" + request.getUsername() + "'");
                System.out.println("Username is null: " + (request.getUsername() == null));
                System.out.println("Password is null: " + (request.getPassword() == null));
            }
            System.out.println("===========================");
            
            // Null checks first
            if (request == null) {
                System.out.println("ERROR: Login request object is null");
                return new AuthResponse("Invalid request");
            }
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                System.out.println("ERROR: Login username validation failed");
                return new AuthResponse("Username is required");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                System.out.println("ERROR: Login password validation failed");
                return new AuthResponse("Password is required");
            }
            
            System.out.println("Login validations passed. Finding user...");
            
            // Find user by username
            User user = userRepository.findByUsername(request.getUsername().trim())
                    .orElse(null);
            
            if (user == null) {
                System.out.println("ERROR: User not found with username: " + request.getUsername().trim());
                return new AuthResponse("Invalid username or password");
            }
            
            System.out.println("User found. Verifying password...");
            
            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                System.out.println("ERROR: Password verification failed");
                return new AuthResponse("Invalid username or password");
            }
            
            System.out.println("Password verified. Generating token...");
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUsername());
            
            System.out.println("Login successful for user: " + user.getUsername());
            return new AuthResponse(token, user.getUsername());
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Login error: " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getName());
            e.printStackTrace();
            return new AuthResponse("Login failed: " + e.getMessage());
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }
}
