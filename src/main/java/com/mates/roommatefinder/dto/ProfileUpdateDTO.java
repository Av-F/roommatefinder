package com.mates.roommatefinder.dto;

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
public class ProfileUpdateDTO {
    private String name;
    private String bio;
    private Integer age;
    private String city;
    private String lookingForOption;
}