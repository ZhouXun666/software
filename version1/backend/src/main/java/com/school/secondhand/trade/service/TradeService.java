package com.school.secondhand.trade.service;

import com.school.secondhand.trade.dto.TradeDTO;
import com.school.secondhand.trade.dto.TradeRequestDTO;

import java.util.List;

public interface TradeService {

    /**
     * 申请交易
     */
    TradeDTO applyTrade(TradeRequestDTO request);

    /**
     * 接受交易
     */
    TradeDTO acceptTrade(Long tradeId);

    /**
     * 拒绝交易
     */
    TradeDTO rejectTrade(Long tradeId);

    /**
     * 取消交易
     */
    TradeDTO cancelTrade(Long tradeId);

    /**
     * 完成交易
     */
    TradeDTO completeTrade(Long tradeId);

    /**
     * 根据ID获取交易详情
     */
    TradeDTO getTradeById(Long tradeId);

    /**
     * 获取用户作为买家的交易列表
     */
    List<TradeDTO> getTradesAsBuyer(Long userId);

    /**
     * 获取用户作为卖家的交易列表
     */
    List<TradeDTO> getTradesAsSeller(Long userId);

    /**
     * 获取商品的交易记录
     */
    List<TradeDTO> getTradesByProductId(Long productId);

    /**
     * 根据状态获取交易列表
     */
    List<TradeDTO> getTradesByStatus(Integer status);

}