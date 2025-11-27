package com.school.secondhand.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private String images;
    private Integer categoryId;
    private Long userId;

}