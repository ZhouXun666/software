package com.school.secondhand.trade.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "trade")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    private String message;

    private Integer status; // 0: 待确认, 1: 已接受, 2: 已拒绝, 3: 已完成, 4: 已取消

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

    private Date acceptTime;

    private Date rejectTime;

    private Date completeTime;

    private Date cancelTime;
}