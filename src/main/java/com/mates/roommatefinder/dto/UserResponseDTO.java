package com.mates.roommatefinder.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;

    public UserResponseDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}