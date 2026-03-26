package com.mates.roommatefinder.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mates.roommatefinder.dto.AuthResponseDTO;
import com.mates.roommatefinder.dto.UserRequestDTO;
import com.mates.roommatefinder.dto.UserResponseDTO;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /*createUser
    @param UserRequestDTO
    creates the user based on the dto given */
    public UserResponseDTO createUser(UserRequestDTO dto) {

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return mapToDTO(userRepository.save(user));
    }
    /*getAllusers
    retrieves a list of all users */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    /*login
    @param email
    @param password
    finds user by chiecking their email and if the password matches, log in the user */
    public AuthResponseDTO login(UserRequestDTO dto) {

    User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid password");
    }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(token);
    }
    
    /*mapToDTO
    @param user
    maps the user to a userDTO*/
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}