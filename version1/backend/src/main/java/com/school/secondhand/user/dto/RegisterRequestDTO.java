package com.school.secondhand.user.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;

}