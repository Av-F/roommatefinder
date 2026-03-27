package com.mates.roommatefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Long id;
    private String name;
    private String lookingForOption;
    private String bio;
    private Integer age;
    private String city;
    private Long userId; // optional: just the id of the user
}