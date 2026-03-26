package com.mates.roommatefinder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=3)
    @NotNull(message="Username can not be null.")
    @NotBlank(message = "Username can not be blank.")
    private String username;
    
    @NotNull(message="Email can not be null.")
    @NotBlank(message = "Email can not be blank.")
    @Column(unique = true)
    private String email;
    
    @Size(min = 6, message = "Password must be at least six characters long." )
    @NotNull(message="Password can not be null.")
    @NotBlank(message = "Password can not be blank.")
    private String password;

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
    } 
}