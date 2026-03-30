package com.mates.roommatefinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String lookingForOption;

    @NotBlank
    private String bio;

    @NotNull
    private Integer age;

    @NotBlank
    private String city;

    @NotNull
    private Long userId;
}