package com.mates.roommatefinder.service;

import java.util.List;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.model.Match;
import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.MatchRepository;
import com.mates.roommatefinder.repository.ProfileRepository;
import com.mates.roommatefinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public List<ProfileResponseDTO> getMatches(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Match> matches = matchRepository.findByUser1OrUser2(user, user);

        return matches.stream()
                .map(match -> {
                    User otherUser = match.getUser1().equals(user)
                            ? match.getUser2()
                            : match.getUser1();

                    Profile profile = profileRepository.findById(otherUser.getId())
                            .orElseThrow();

                    return ProfileResponseDTO.fromProfile(profile);
                })
                .toList();
    }
}