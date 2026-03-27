package com.mates.roommatefinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileDTO {

    @NotBlank(message="Name can not be blank")
    private String name;

    @NotBlank(message="LookingForOption can not be blank")
    private String lookingForOption;

    @NotBlank(message="Bio can not be blank")
    private String bio;

    @NotNull(message="Age can not be blank")
    private Integer age;

    @NotBlank(message="City can not be blank")
    private String city;

    @NotNull(message="id can not be null")
    private Long id;
}