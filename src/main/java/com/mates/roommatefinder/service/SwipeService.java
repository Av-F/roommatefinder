package com.mates.roommatefinder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.ProfileRepository;
import com.mates.roommatefinder.repository.SwipeRepository;
import com.mates.roommatefinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    /**
     * Get a list of profiles the user can swipe on (same city, not already swiped)
     */
    public List<ProfileResponseDTO> getSwipeableProfiles(Long userId) {
        User swiper = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile swiperProfile = profileRepository.findById(swiper.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // All users in the same city
        List<User> sameCityUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId)) // exclude self
                .filter(u -> {
                    Profile profile = profileRepository.findById(u.getId()).orElse(null);
                    return profile != null && profile.getCity().equals(swiperProfile.getCity());
                })
                .collect(Collectors.toList());

        // Remove users already swiped on
        List<Swipe> swipesByUser = swipeRepository.findBySwiper(swiper);
        List<Long> swipedIds = swipesByUser.stream()
                .map(s -> s.getTarget().getId())
                .toList();

        List<User> toSwipe = sameCityUsers.stream()
                .filter(u -> !swipedIds.contains(u.getId()))
                .collect(Collectors.toList());

        // Map to ProfileResponseDTO
        return toSwipe.stream()
                .map(u -> profileRepository.findById(u.getId())
                        .orElseThrow(() -> new RuntimeException("Profile not found")))
                .map(ProfileResponseDTO::fromProfile)
                .toList();
    }

    /**
     * Swipe on a user (like or pass)
     */
    @Transactional
    public void swipe(Long swiperId, Long targetId, boolean liked) {
        User swiper = userRepository.findById(swiperId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        // Prevent duplicate swipe
        if (!swipeRepository.findBySwiperAndTarget(swiper, target).isEmpty()) {
            throw new RuntimeException("Already swiped on this user");
        }

        Swipe swipe = Swipe.builder()
                .swiper(swiper)
                .target(target)
                .liked(liked)
                .build();

        swipeRepository.save(swipe);
    }

    /**
     * Check if a match exists (both liked each other)
     */
    public boolean isMatch(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean user1Liked2 = swipeRepository.findBySwiperAndTarget(user1, user2).stream()
                .anyMatch(Swipe::getLiked);

        boolean user2Liked1 = swipeRepository.findBySwiperAndTarget(user2, user1).stream()
                .anyMatch(Swipe::getLiked);

        return user1Liked2 && user2Liked1;
    }
}