package com.mates.roommatefinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProfileDTO {
    @NotBlank(message="Name cannot be blank")
    private String name;

    @NotBlank(message="LookingForOption cannot be blank")
    private String lookingForOption;

    @NotBlank(message="Bio cannot be blank")
    private String bio;

    @NotNull(message="Age cannot be blank")
    private Integer age;

    @NotBlank(message="City cannot be blank")
    private String city;

    @NotNull(message="UserId cannot be null")
    private Long userId; // Pass existing user ID
}