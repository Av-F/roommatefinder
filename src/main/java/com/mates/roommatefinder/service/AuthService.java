package com.mates.roommatefinder.service;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.dto.AuthRequest;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.UserRepository;
import com.mates.roommatefinder.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtil;

    public String login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateJwtToken(user.getId());
    }

    public String register(User user) {
        User savedUser = userRepository.save(user);
        return jwtUtil.generateJwtToken(savedUser.getId());
    }
}