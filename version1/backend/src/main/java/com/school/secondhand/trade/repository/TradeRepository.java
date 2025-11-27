package com.school.secondhand.trade.repository;

import com.school.secondhand.trade.dto.TradeDTO;
import com.school.secondhand.trade.entity.Trade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * 根据商品ID查询交易记录
     */
    List<Trade> findByProductId(Long productId);

    /**
     * 根据买家ID查询交易记录
     */
    List<Trade> findByBuyerId(Long buyerId);

    /**
     * 根据卖家ID查询交易记录
     */
    List<Trade> findBySellerId(Long sellerId);

    /**
     * 根据状态查询交易记录
     */
    List<Trade> findByStatus(Integer status);

    /**
     * 根据买家ID和状态查询交易记录
     */
    List<Trade> findByBuyerIdAndStatus(Long buyerId, Integer status);

    /**
     * 根据卖家ID和状态查询交易记录
     */
    List<Trade> findBySellerIdAndStatus(Long sellerId, Integer status);

    /**
     * 根据商品ID和买家ID查询交易记录
     */
    Trade[] findByProductIdAndBuyerId(Long productId, Long buyerId);

}