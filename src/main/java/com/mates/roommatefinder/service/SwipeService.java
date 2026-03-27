package com.mates.roommatefinder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.model.Match;
import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.ProfileRepository;
import com.mates.roommatefinder.repository.SwipeRepository;
import com.mates.roommatefinder.repository.UserRepository;
import com.mates.roommatefinder.repository.MatchRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final MatchRepository matchRepository;

    /**
     * Get a list of profiles the user can swipe on:
     *  • same city
     *  • excluding self
     *  • excluding already swiped users
     */
    public List<ProfileResponseDTO> getSwipeableProfiles(Long userId) {
        // Fetch user
        User swiper = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch swiper's profile
        Profile swiperProfile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Fetch all profiles in same city (except the calling user)
        List<Profile> cityProfiles = profileRepository.findByCityAndIdNot(swiperProfile.getCity(), userId);

        // Find IDs the user has already swiped on
        List<Long> swipedIds = swipeRepository.findBySwiper(swiper).stream()
                .map(swipe -> swipe.getTarget().getId())
                .collect(Collectors.toList());

        // Filter out profiles already swiped on
        List<Profile> available = cityProfiles.stream()
                .filter(p -> !swipedIds.contains(p.getId()))
                .collect(Collectors.toList());

        // Convert to DTO
        return available.stream()
                .map(ProfileResponseDTO::fromProfile)
                .collect(Collectors.toList());
    }

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

        // Check for match
        if (liked) {
            boolean targetLikedSwiper = swipeRepository.findBySwiperAndTarget(target, swiper).stream()
                    .anyMatch(Swipe::getLiked);

            // Only create match if both like each other and match does not exist
            if (targetLikedSwiper && !matchRepository.existsByUser1AndUser2(swiper, target) 
                && !matchRepository.existsByUser1AndUser2(target, swiper)) {

                Match match = Match.builder()
                        .user1(swiper)
                        .user2(target)
                        .build();

                matchRepository.save(match);
            }
        }
    }

    public boolean isMatch(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean user1Liked2 = swipeRepository.findBySwiperAndTarget(user1, user2).stream()
                .anyMatch(Swipe::getLiked);

        boolean user2Liked1 = swipeRepository.findBySwiperAndTarget(user2, user1).stream()
                .anyMatch(Swipe::getLiked);

        return user1Liked2 && user2Liked1;
    }
}