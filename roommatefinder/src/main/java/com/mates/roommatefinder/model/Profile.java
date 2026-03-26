package com.mates.roommatefinder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="name can not be blank")
    private String name;

    @NotBlank(message="option can not be blank")
    private String lookingForOption;
    
    @NotBlank(message="Bio can not be blank")
    private String bio;

    @NotNull(message="age can not be blank")
    private Integer age;

    @NotBlank(message="city can not be blank")
    private String city;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;    
}