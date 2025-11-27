package com.school.secondhand.user.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String token;
    private UserDTO user;

}