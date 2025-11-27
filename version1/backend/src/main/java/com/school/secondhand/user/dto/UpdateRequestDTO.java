package com.school.secondhand.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateRequestDTO {
    private String username;
    private String name;
    private Integer gender;
    private String email;
    private String phone;
}
