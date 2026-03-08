package com.example.clothing.service.impl;

import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.Trade;
import com.example.clothing.repository.ClothingRepository;
import com.example.clothing.repository.TradeRepository;
import com.example.clothing.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ClothingRepository clothingRepository;

    @Override
    public List<Clothing> getClothingList(String category, String size, String condition, String priceRange, String sortBy, int page, int sizePerPage) {
        // 构建查询条件
        List<Clothing> clothingList = clothingRepository.findAll();

        // 应用筛选
        if (category != null && !category.isEmpty()) {
            clothingList = clothingList.stream()
                    .filter(c -> category.equals(c.getType()))
                    .toList();
        }

        if (size != null && !size.isEmpty()) {
            clothingList = clothingList.stream()
                    .filter(c -> size.equals(c.getSize()))
                    .toList();
        }

        if (condition != null && !condition.isEmpty()) {
            clothingList = clothingList.stream()
                    .filter(c -> condition.equals(c.getStatus()))
                    .toList();
        }

        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            if (range.length == 2) {
                double minPrice = Double.parseDouble(range[0]);
                double maxPrice = Double.parseDouble(range[1].replace("+", ""));
                clothingList = clothingList.stream()
                        .filter(c -> c.getPrice() >= minPrice && c.getPrice() <= maxPrice)
                        .toList();
            }
        }

        // 应用排序
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "latest":
                    clothingList.sort((a, b) -> b.getId().compareTo(a.getId()));
                    break;
                case "price-asc":
                    clothingList.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                    break;
                case "price-desc":
                    clothingList.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                    break;
            }
        }

        // 应用分页
        int start = (page - 1) * sizePerPage;
        int end = Math.min(start + sizePerPage, clothingList.size());
        if (start >= clothingList.size()) {
            return List.of();
        }
        return clothingList.subList(start, end);
    }

    @Override
    @Transactional
    public Trade createTrade(Trade trade) {
        // 验证服装是否存在且可用
        Optional<Clothing> clothingOptional = clothingRepository.findById(trade.getClothingId());
        if (clothingOptional.isEmpty()) {
            throw new RuntimeException("服装不存在");
        }

        Clothing clothing = clothingOptional.get();
        if (!"available".equals(clothing.getStatus())) {
            throw new RuntimeException("服装不可用");
        }

        // 设置卖家ID为服装的所有者
        trade.setSellerId(clothing.getUserId());
        // 设置初始状态为待处理
        trade.setStatus("pending");

        // 保存交易
        Trade savedTrade = tradeRepository.save(trade);

        // 更新服装状态为已交易
        clothing.setStatus("trading");
        clothingRepository.save(clothing);

        return savedTrade;
    }

    @Override
    public List<Trade> getTradeList(Long userId) {
        // 获取用户作为买家或卖家的交易
        return tradeRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Override
    public Trade getTradeById(Long tradeId, Long userId) {
        Optional<Trade> tradeOptional = tradeRepository.findById(tradeId);
        if (tradeOptional.isEmpty()) {
            throw new RuntimeException("交易不存在");
        }

        Trade trade = tradeOptional.get();
        // 验证用户是否是交易的买家或卖家
        if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
            throw new RuntimeException("无权访问此交易");
        }

        return trade;
    }

    @Override
    @Transactional
    public Trade updateTradeStatus(Long tradeId, String status, Long userId) {
        Trade trade = getTradeById(tradeId, userId);

        // 验证状态更新的合法性
        if (!trade.getSellerId().equals(userId)) {
            throw new RuntimeException("只有卖家可以更新交易状态");
        }

        // 验证状态流转的合法性
        if ("pending".equals(trade.getStatus()) && ("completed".equals(status) || "cancelled".equals(status))) {
            trade.setStatus(status);

            // 如果交易完成，更新服装状态
            if ("completed".equals(status)) {
                Optional<Clothing> clothingOptional = clothingRepository.findById(trade.getClothingId());
                if (clothingOptional.isPresent()) {
                    Clothing clothing = clothingOptional.get();
                    clothing.setStatus("sold");
                    clothingRepository.save(clothing);
                }
            }

            return tradeRepository.save(trade);
        } else {
            throw new RuntimeException("状态更新不合法");
        }
    }

    @Override
    @Transactional
    public Trade completeTrade(Long tradeId, Long userId) {
        Trade trade = getTradeById(tradeId, userId);

        // 验证用户是否是交易的买家
        if (!trade.getBuyerId().equals(userId)) {
            throw new RuntimeException("只有买家可以确认交易完成");
        }

        // 验证交易状态是否为待处理
        if (!"pending".equals(trade.getStatus())) {
            throw new RuntimeException("交易状态不合法");
        }

        // 更新交易状态为已完成
        trade.setStatus("completed");
        Trade updatedTrade = tradeRepository.save(trade);

        // 更新服装状态为已售出
        Optional<Clothing> clothingOptional = clothingRepository.findById(trade.getClothingId());
        if (clothingOptional.isPresent()) {
            Clothing clothing = clothingOptional.get();
            clothing.setStatus("sold");
            clothingRepository.save(clothing);
        }

        return updatedTrade;
    }
}
