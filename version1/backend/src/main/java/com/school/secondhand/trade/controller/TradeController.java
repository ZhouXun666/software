package com.school.secondhand.trade.controller;

import com.school.secondhand.trade.dto.TradeDTO;
import com.school.secondhand.trade.dto.TradeRequestDTO;
import com.school.secondhand.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    /**
     * 申请交易
     */
    @PostMapping("/apply")
    public TradeDTO applyTrade(@RequestBody TradeRequestDTO request) {
        return tradeService.applyTrade(request);
    }

    /**
     * 接受交易
     */
    @PostMapping("/{id}/accept")
    public TradeDTO acceptTrade(@PathVariable("id") Long tradeId) {
        return tradeService.acceptTrade(tradeId);
    }

    /**
     * 拒绝交易
     */
    @PostMapping("/{id}/reject")
    public TradeDTO rejectTrade(@PathVariable("id") Long tradeId) {
        return tradeService.rejectTrade(tradeId);
    }

    /**
     * 取消交易
     */
    @PostMapping("/{id}/cancel")
    public TradeDTO cancelTrade(@PathVariable("id") Long tradeId) {
        return tradeService.cancelTrade(tradeId);
    }

    /**
     * 完成交易
     */
    @PostMapping("/{id}/complete")
    public TradeDTO completeTrade(@PathVariable("id") Long tradeId) {
        return tradeService.completeTrade(tradeId);
    }

    /**
     * 获取交易详情
     */
    @GetMapping("/{id}")
    public TradeDTO getTradeById(@PathVariable("id") Long tradeId) {
        return tradeService.getTradeById(tradeId);
    }

    /**
     * 获取用户作为买家的交易列表
     */
    @GetMapping("/buyer/{id}")
    public List<TradeDTO> getTradesAsBuyer(@PathVariable("id") Long id) {
        return tradeService.getTradesAsBuyer(id);
    }

    /**
     * 获取用户作为卖家的交易列表
     */
    @GetMapping("/seller/{id}")
    public List<TradeDTO> getTradesAsSeller(@PathVariable("id") Long id) {
        return tradeService.getTradesAsSeller(id);
    }

    /**
     * 获取商品的交易记录
     */
    @GetMapping("/product/{productId}")
    public List<TradeDTO> getTradesByProductId(@PathVariable("productId") Long productId) {
        return tradeService.getTradesByProductId(productId);
    }

    /**
     * 根据状态获取交易列表
     */
    @GetMapping("/status/{status}")
    public List<TradeDTO> getTradesByStatus(@PathVariable("status") Integer status) {
        return tradeService.getTradesByStatus(status);
    }

}