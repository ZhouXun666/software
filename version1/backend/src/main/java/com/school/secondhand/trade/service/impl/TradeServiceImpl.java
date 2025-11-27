package com.school.secondhand.trade.service.impl;

import com.school.secondhand.trade.dto.TradeDTO;
import com.school.secondhand.trade.dto.TradeRequestDTO;
import com.school.secondhand.trade.entity.Trade;
import com.school.secondhand.trade.repository.TradeRepository;
import com.school.secondhand.trade.service.TradeService;
import com.school.secondhand.product.entity.Product;
import com.school.secondhand.product.repository.ProductRepository;
import com.school.secondhand.user.entity.User;
import com.school.secondhand.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public TradeDTO applyTrade(TradeRequestDTO request) {
        // 检查商品是否存在且可交易
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStatus() != 1) {
            throw new RuntimeException("该商品不可交易");
        }

        if (product.getUserId().equals(request.getBuyerId())) {
            throw new RuntimeException("不能购买自己的商品");
        }

        // 检查是否已存在交易申请
        Trade existingTrade[] = tradeRepository.findByProductIdAndBuyerId(request.getProductId(), request.getBuyerId());
        for(Trade trade:existingTrade){
            if (trade.getStatus() == 0) {
                throw new RuntimeException("您已经申请过交易");
            }
        }

        // 创建交易记录
        Trade trade = new Trade();
        trade.setProductId(request.getProductId());
        trade.setBuyerId(request.getBuyerId());
        trade.setSellerId(product.getUserId());
        trade.setMessage(request.getMessage());
        trade.setAcceptTime(null);
        trade.setRejectTime(null);
        trade.setCancelTime(null);
        trade.setCompleteTime(null);
        trade.setStatus(0); // 待确认

        tradeRepository.save(trade);

        return convertToDTO(trade);
    }

    @Override
    @Transactional
    public TradeDTO acceptTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));

        if (trade.getStatus() != 0) {
            throw new RuntimeException("该交易状态不允许接受");
        }

        // 更新交易状态
        trade.setStatus(1); // 已接受
        trade.setAcceptTime(new Date());
        tradeRepository.save(trade);

        // 更新商品状态为已售出
        Product product = productRepository.findById(trade.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setStatus(2); // 已售出
        productRepository.save(product);

        return convertToDTO(trade);
    }

    @Override
    public TradeDTO rejectTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));

        if (trade.getStatus() != 0) {
            throw new RuntimeException("该交易状态不允许拒绝");
        }

        trade.setStatus(2); // 已拒绝
        trade.setRejectTime(new Date());
        tradeRepository.save(trade);

        return convertToDTO(trade);
    }

    @Override
    public TradeDTO cancelTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));

        if (trade.getStatus() != 0 && trade.getStatus() != 1) {
            throw new RuntimeException("该交易状态不允许取消");
        }

        trade.setStatus(4); // 已取消
        trade.setCancelTime(new Date());
        tradeRepository.save(trade);

        // 如果交易已接受，需要将商品状态改为上架
        if (trade.getStatus() == 1) {
            Product product = productRepository.findById(trade.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            product.setStatus(1); // 上架
            productRepository.save(product);
        }

        return convertToDTO(trade);
    }

    @Override
    public TradeDTO completeTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));

        if (trade.getStatus() != 1) {
            throw new RuntimeException("该交易状态不允许完成");
        }

        trade.setStatus(3); // 已完成
        trade.setCompleteTime(new Date());
        tradeRepository.save(trade);

        return convertToDTO(trade);
    }

    @Override
    public TradeDTO getTradeById(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("交易记录不存在"));

        return convertToDTO(trade);
    }

    @Override
    public List<TradeDTO> getTradesAsBuyer(Long userId) {
        List<Trade> trades = tradeRepository.findByBuyerId(userId);
        return trades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeDTO> getTradesAsSeller(Long userId) {
        List<Trade> trades = tradeRepository.findBySellerId(userId);
        return trades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeDTO> getTradesByProductId(Long productId) {
        List<Trade> trades = tradeRepository.findByProductId(productId);
        return trades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TradeDTO> getTradesByStatus(Integer status) {
        List<Trade> trades = tradeRepository.findByStatus(status);
        return trades.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将Trade实体转换为TradeDTO
     */
    private TradeDTO convertToDTO(Trade trade) {
        TradeDTO dto = new TradeDTO();
        BeanUtils.copyProperties(trade, dto);

        // 填充商品信息
        Product product = productRepository.findById(trade.getProductId())
                .orElse(null);
        if (product != null) {
            dto.setProductName(product.getName());
            dto.setProductImages(product.getImages());
            dto.setProductPrice(product.getPrice());
            dto.setProductCategory(product.getCategoryId());
            dto.setProductDescription(product.getDescription());
        }

        // 填充买家信息
        User buyer = userRepository.findById(trade.getBuyerId())
                .orElse(null);
        if (buyer != null) {
            dto.setBuyerName(buyer.getUsername());
            dto.setBuyerPhone(buyer.getPhone());
        }

        // 填充卖家信息
        User seller = userRepository.findById(trade.getSellerId())
                .orElse(null);
        if (seller != null) {
            dto.setSellerName(seller.getUsername());
            dto.setSellerPhone(seller.getPhone());
        }

        // 设置状态文本
        dto.setStatusText(getStatusText(trade.getStatus()));

        return dto;
    }

    /**
     * 获取交易状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "待确认";
            case 1:
                return "已接受";
            case 2:
                return "已拒绝";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}