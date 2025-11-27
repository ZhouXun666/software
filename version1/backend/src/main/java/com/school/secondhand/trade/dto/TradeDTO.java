package com.school.secondhand.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeDTO {
    private Long id;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private String message;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Date acceptTime;
    private Date cancelTime;
    private Date rejectTime;
    private Date completeTime;

    private String productName;
    private String productImages;
    private BigDecimal productPrice;
    private Integer productCategory;
    private String productDescription;
    private String buyerName;
    private String buyerPhone;
    private String sellerName;
    private String sellerPhone;
    private String statusText;

}