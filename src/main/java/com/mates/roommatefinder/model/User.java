package com.mates.roommatefinder.model;

import com.mates.roommatefinder.security.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "password")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @NotNull(message = "Username cannot be null")
    private String username;
     @NotBlank(message = "Email is required")
    @NotNull(message = "Email cannot be null")
    private String email;
     @NotBlank(message = "Password is required")
    @NotNull(message = "Password cannot be null")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    // Link to Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    // Convenience getter
    public Profile getProfile() {
        return profile;
    }
}