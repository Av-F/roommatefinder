package com.mates.roommatefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {

    private Long id;
    private String name;
    private String lookingForOption;
    private String bio;
    private Integer age;
    private String city;
    private Long userId;

}