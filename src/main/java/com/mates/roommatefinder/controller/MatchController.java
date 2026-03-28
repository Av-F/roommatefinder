package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.security.SecurityUtils;
import com.mates.roommatefinder.service.MatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Slf4j
public class MatchController {

    private final MatchService matchService;

    /**
     * Get all matches for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<ProfileResponseDTO>> getMyMatches() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ProfileResponseDTO> matches = matchService.getMatches(currentUserId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            log.error("Failed to get matches for user {}: {}", currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get matches for a specific user (deprecated - use GET /api/matches)
     * Authorization: user can only view their own matches
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ProfileResponseDTO>> getMatches(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only view their own matches
        if (!currentUserId.equals(userId)) {
            log.warn("Unauthorized access: User {} attempted to get matches for user {}", currentUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            List<ProfileResponseDTO> matches = matchService.getMatches(userId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            log.error("Failed to get matches for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}