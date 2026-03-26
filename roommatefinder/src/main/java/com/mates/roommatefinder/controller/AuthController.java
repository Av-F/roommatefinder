package com.mates.roommatefinder.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.AuthResponseDTO;
import com.mates.roommatefinder.dto.UserRequestDTO;
import com.mates.roommatefinder.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody UserRequestDTO dto) {
        return userService.login(dto);
    }
}