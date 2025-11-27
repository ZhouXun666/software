package com.school.secondhand.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Integer role;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}