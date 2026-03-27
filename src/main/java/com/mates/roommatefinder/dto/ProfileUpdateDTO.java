package com.mates.roommatefinder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDTO {
    private String name;
    private String bio;
    private Integer age;
    private String city;
    private String lookingForOption;
}