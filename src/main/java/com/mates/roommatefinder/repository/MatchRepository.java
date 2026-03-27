package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.Match;
import com.mates.roommatefinder.model.User;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser1OrUser2(User user1, User user2);
    boolean existsByUser1AndUser2(User user1, User user2);
    boolean existsByUser2AndUser1(User user2, User user1);
}