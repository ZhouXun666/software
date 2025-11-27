package com.school.secondhand.trade.dto;

import lombok.Data;

@Data
public class TradeRequestDTO {

    private Long productId;
    private Long buyerId;
    private String message;

}