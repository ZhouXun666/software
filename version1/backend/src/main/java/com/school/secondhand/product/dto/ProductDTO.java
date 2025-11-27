package com.school.secondhand.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String images;
    private Integer categoryId;
    private Long userId;
    private Integer status;
    private Integer views;
    private Date createTime;
    private Date updateTime;
    private String username;
    private String userphone;
}