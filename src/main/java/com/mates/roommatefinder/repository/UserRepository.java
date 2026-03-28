package com.mates.roommatefinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

     Optional<User> findByUsername(String username);
}