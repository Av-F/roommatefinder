package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByCity(String city);
    List<Profile> findByAgeBetween(Integer minAge, Integer maxAge);
    List<Profile> findByLookingForOption(String lookingForOption);
}