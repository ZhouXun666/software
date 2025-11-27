package com.school.secondhand.user.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    private Integer gender;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;


    private Integer role; // 0: 普通用户, 1: 管理员

    private Integer status; // 0: 禁用, 1: 启用

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

}