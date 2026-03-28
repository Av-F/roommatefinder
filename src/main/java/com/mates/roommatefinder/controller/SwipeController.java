package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.security.SecurityUtils;
import com.mates.roommatefinder.service.SwipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
@Slf4j
public class SwipeController {

    private final SwipeService swipeService;

    /**
     * Get swipeable profiles for current authenticated user
     */
    @GetMapping("/discover")
    public ResponseEntity<List<ProfileResponseDTO>> getSwipeableProfiles() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ProfileResponseDTO> profiles = swipeService.getSwipeableProfiles(currentUserId);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Failed to get swipeable profiles for user {}: {}", currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get swipeable profiles for a specific user (deprecated - use /discover)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ProfileResponseDTO>> getSwipeableProfiles(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only view profiles for themselves
        if (!currentUserId.equals(userId)) {
            log.warn("Unauthorized access: User {} attempted to get swipes for user {}", currentUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            List<ProfileResponseDTO> profiles = swipeService.getSwipeableProfiles(userId);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Failed to get swipeable profiles for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Swipe on a user profile
     * Current user must be the swiper
     */
    @PostMapping("/like/{targetId}")
    public ResponseEntity<Void> likeProfile(@PathVariable Long targetId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            swipeService.swipe(currentUserId, targetId, true);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to like profile {} by user {}: {}", targetId, currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Pass on a user profile (don't like)
     */
    @PostMapping("/pass/{targetId}")
    public ResponseEntity<Void> passProfile(@PathVariable Long targetId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            swipeService.swipe(currentUserId, targetId, false);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to pass on profile {} by user {}: {}", targetId, currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Swipe on a user (deprecated - use /like/{id} or /pass/{id})
     */
    @PostMapping("/{swiperId}/{targetId}")
    public ResponseEntity<Void> swipe(@PathVariable Long swiperId,
                                      @PathVariable Long targetId,
                                      @RequestParam boolean liked) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only swipe for themselves
        if (!currentUserId.equals(swiperId)) {
            log.warn("Unauthorized swipe: User {} attempted to swipe for user {}", currentUserId, swiperId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            swipeService.swipe(swiperId, targetId, liked);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to swipe {} for user {} on user {}: {}", 
                     liked ? "like" : "pass", swiperId, targetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Check if two users have matched
     * User must be one of the users in the match check
     */
    @GetMapping("/match/{user1}/{user2}")
    public ResponseEntity<Boolean> checkMatch(@PathVariable Long user1, @PathVariable Long user2) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authorization: user can only check match if they are one of the users
        if (!currentUserId.equals(user1) && !currentUserId.equals(user2)) {
            log.warn("Unauthorized match check: User {} attempted to check match between {} and {}", 
                     currentUserId, user1, user2);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            boolean isMatch = swipeService.isMatch(user1, user2);
            return ResponseEntity.ok(isMatch);
        } catch (Exception e) {
            log.error("Failed to check match between {} and {}: {}", user1, user2, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}