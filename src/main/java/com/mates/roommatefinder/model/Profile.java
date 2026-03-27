package com.mates.roommatefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
@ToString(exclude = "user")
public class Profile {
    
    @Id
    private Long id; // same as user id

    @OneToOne
    @MapsId // maps Profile.id to User.id
    @JoinColumn(name = "id")
    @JsonIgnore
    private User user;

    private String name;
    private String bio;
    private Integer age;
    private String city;
    private String lookingForOption;
}