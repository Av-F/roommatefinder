package com.mates.roommatefinder.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.model.Match;
import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.MatchRepository;
import com.mates.roommatefinder.repository.SwipeRepository;
import com.mates.roommatefinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public void swipe(Long swiperId, Long targetId, boolean liked) {

        if (swiperId.equals(targetId)) {
            throw new RuntimeException("You cannot swipe on yourself");
        }

        User swiper = userRepository.findById(swiperId)
                .orElseThrow(() -> new RuntimeException("Swiper not found"));

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target not found"));

        // Prevent duplicate swipes
        if (swipeRepository.findBySwiperAndTarget(swiper, target).isPresent()) {
            throw new RuntimeException("You already swiped on this user");
        }

        // Save swipe
        Swipe swipe = Swipe.builder()
                .swiper(swiper)
                .target(target)
                .liked(liked)
                .build();

        swipeRepository.save(swipe);

        // Check for match ONLY if liked
        if (liked) {
            Optional<Swipe> reverseSwipe =
                    swipeRepository.findBySwiperAndTarget(target, swiper);

            if (reverseSwipe.isPresent() && Boolean.TRUE.equals(reverseSwipe.get().getLiked())) {

                // Prevent duplicate matches
                boolean alreadyMatched = matchRepository
                        .findByUser1OrUser2(swiper, swiper)
                        .stream()
                        .anyMatch(m ->
                                (m.getUser1().equals(swiper) && m.getUser2().equals(target)) ||
                                (m.getUser1().equals(target) && m.getUser2().equals(swiper))
                        );

                if (!alreadyMatched) {
                    Match match = Match.builder()
                            .user1(swiper)
                            .user2(target)
                            .build();

                    matchRepository.save(match);
                }
            }
        }
    }
}