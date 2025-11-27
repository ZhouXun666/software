package com.school.secondhand.product.entity;

import com.sun.org.glassfish.external.statistics.annotations.Reset;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 255)
    private String images;

    private Integer categoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Integer status; // 0: 下架, 1: 上架, 2: 已售出

    private Integer views; // 浏览量

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

}