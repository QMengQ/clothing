package com.example.clothing.service;

import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.Trade;
import java.util.List;

public interface TradeService {

    // 获取服装列表，支持筛选和排序
    List<Clothing> getClothingList(String category, String size, String condition, String priceRange, String sortBy, int page, int sizePerPage);

    // 创建交易
    Trade createTrade(Trade trade);

    // 获取用户的交易列表
    List<Trade> getTradeList(Long userId);

    // 获取交易详情
    Trade getTradeById(Long tradeId, Long userId);

    // 更新交易状态
    Trade updateTradeStatus(Long tradeId, String status, Long userId);

    // 确认交易完成
    Trade completeTrade(Long tradeId, Long userId);
}
