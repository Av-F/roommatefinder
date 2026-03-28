package com.mates.roommatefinder.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.AuthRequest;
import com.mates.roommatefinder.dto.AuthResponse;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.UserRepository;
import com.mates.roommatefinder.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // ---------------- Register ----------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email already in use");
            }

            // Hash password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save user
            User savedUser = userRepository.save(user);

            // Generate JWT with role
            String token = jwtUtils.generateJwtToken(savedUser.getId(), savedUser.getRole());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            // Check password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            // Generate JWT with role
            String token = jwtUtils.generateJwtToken(user.getId(), user.getRole());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }
}