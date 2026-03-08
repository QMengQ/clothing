package com.example.clothing.repository;

import com.example.clothing.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    // 根据买家ID或卖家ID查询交易
    List<Trade> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);

    // 根据服装ID查询交易
    List<Trade> findByClothingId(Long clothingId);

    // 根据买家ID和状态查询交易
    List<Trade> findByBuyerIdAndStatus(Long buyerId, String status);

    // 根据卖家ID和状态查询交易
    List<Trade> findBySellerIdAndStatus(Long sellerId, String status);
}
