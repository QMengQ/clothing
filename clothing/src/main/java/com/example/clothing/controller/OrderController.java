package com.example.clothing.controller;

import com.example.clothing.entity.Trade;
import com.example.clothing.entity.TradeClothing;
import com.example.clothing.entity.User;
import com.example.clothing.repository.TradeClothingRepository;
import com.example.clothing.repository.TradeRepository;
import com.example.clothing.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeClothingRepository tradeClothingRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取订单列表
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            List<Trade> trades = tradeRepository.findByBuyerIdOrSellerId(userId, userId);
            List<Map<String, Object>> orders = new ArrayList<>();

            for (Trade trade : trades) {
                // 检查状态过滤
                if (status != null && !status.isEmpty() && !trade.getStatus().equals(status)) {
                    continue;
                }

                // 检查搜索条件
                if (search != null && !search.isEmpty()) {
                    TradeClothing clothing = tradeClothingRepository.findById(trade.getClothingId()).orElse(null);
                    if (clothing == null || !clothing.getTitle().toLowerCase().contains(search.toLowerCase())) {
                        continue;
                    }
                }

                Map<String, Object> order = new HashMap<>();
                order.put("id", trade.getId());
                order.put("createTime", trade.getCreatedAt());
                order.put("status", trade.getStatus());
                order.put("tradeType", trade.getTradeType());
                order.put("price", trade.getPrice());
                order.put("paymentMethod", trade.getPaymentMethod());
                order.put("buyerId", trade.getBuyerId());
                order.put("sellerId", trade.getSellerId());
                order.put("rentalStartDate", trade.getRentalStartDate());
                order.put("rentalEndDate", trade.getRentalEndDate());

                // 加载服装信息
                TradeClothing clothing = tradeClothingRepository.findById(trade.getClothingId()).orElse(null);
                if (clothing != null) {
                    Map<String, Object> clothingInfo = new HashMap<>();
                    clothingInfo.put("id", clothing.getId());
                    clothingInfo.put("title", clothing.getTitle());
                    clothingInfo.put("description", clothing.getDescription());
                    String imageUrl = "";
                    if (clothing.getImageUrls() != null && !clothing.getImageUrls().isEmpty()) {
                        String firstImage = clothing.getImageUrls().split(",")[0];
                        // 检查是否是完整的URL
                        if (firstImage.startsWith("http")) {
                            imageUrl = firstImage;
                        } else {
                            // 构建完整的本地图片URL
                            imageUrl = "http://localhost:8080/uploads/" + firstImage;
                        }
                    }
                    clothingInfo.put("image", imageUrl);
                    clothingInfo.put("category", clothing.getCategory());
                    clothingInfo.put("size", clothing.getSize());
                    clothingInfo.put("condition", clothing.getClothingCondition());
                    order.put("clothing", clothingInfo);
                }

                // 加载地址信息
                Map<String, Object> address = new HashMap<>();
                address.put("recipient", trade.getRecipient() != null ? trade.getRecipient() : "收件人");
                address.put("phone", trade.getPhone() != null ? trade.getPhone() : "13800138000");
                address.put("province", trade.getProvince() != null ? trade.getProvince() : "北京市");
                address.put("city", trade.getCity() != null ? trade.getCity() : "北京市");
                address.put("district", trade.getDistrict() != null ? trade.getDistrict() : "朝阳区");
                address.put("detailAddress", trade.getDetailAddress() != null ? trade.getDetailAddress() : "详细地址");
                order.put("address", address);

                orders.add(order);
            }

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("获取订单列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取订单列表失败\"}");
        }
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"订单不存在\"}");
            }

            // 验证用户是否有权限查看该订单
            if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限查看该订单\"}");
            }

            Map<String, Object> order = new HashMap<>();
            order.put("id", trade.getId());
            order.put("createTime", trade.getCreatedAt());
            order.put("status", trade.getStatus());
            order.put("tradeType", trade.getTradeType());
            order.put("price", trade.getPrice());
            order.put("paymentMethod", trade.getPaymentMethod());
            order.put("buyerId", trade.getBuyerId());
            order.put("rentalStartDate", trade.getRentalStartDate());
            order.put("rentalEndDate", trade.getRentalEndDate());

            // 加载服装信息
            TradeClothing clothing = tradeClothingRepository.findById(trade.getClothingId()).orElse(null);
            if (clothing != null) {
                Map<String, Object> clothingInfo = new HashMap<>();
                clothingInfo.put("id", clothing.getId());
                clothingInfo.put("title", clothing.getTitle());
                clothingInfo.put("description", clothing.getDescription());
                String imageUrl = "";
                if (clothing.getImageUrls() != null && !clothing.getImageUrls().isEmpty()) {
                    String firstImage = clothing.getImageUrls().split(",")[0];
                    // 检查是否是完整的URL
                    if (firstImage.startsWith("http")) {
                        imageUrl = firstImage;
                    } else {
                        // 构建完整的本地图片URL
                        imageUrl = "http://localhost:8080/uploads/" + firstImage;
                    }
                }
                clothingInfo.put("image", imageUrl);
                clothingInfo.put("category", clothing.getCategory());
                clothingInfo.put("size", clothing.getSize());
                clothingInfo.put("condition", clothing.getClothingCondition());
                order.put("clothing", clothingInfo);
            }

            // 加载地址信息
            Map<String, Object> address = new HashMap<>();
            address.put("recipient", trade.getRecipient() != null ? trade.getRecipient() : "收件人");
            address.put("phone", trade.getPhone() != null ? trade.getPhone() : "13800138000");
            address.put("province", trade.getProvince() != null ? trade.getProvince() : "北京市");
            address.put("city", trade.getCity() != null ? trade.getCity() : "北京市");
            address.put("district", trade.getDistrict() != null ? trade.getDistrict() : "朝阳区");
            address.put("detailAddress", trade.getDetailAddress() != null ? trade.getDetailAddress() : "详细地址");
            order.put("address", address);

            // 加载物流信息（暂时使用模拟数据，后续需要添加物流表）
            List<Map<String, Object>> logistics = new ArrayList<>();
            if ("shipped".equals(trade.getStatus()) || "completed".equals(trade.getStatus())) {
                Map<String, Object> log1 = new HashMap<>();
                log1.put("time", trade.getUpdatedAt());
                log1.put("content", "订单已发货，请注意查收");
                logistics.add(log1);

                Map<String, Object> log2 = new HashMap<>();
                log2.put("time", trade.getUpdatedAt());
                log2.put("content", "快递已揽收，正在派送中");
                logistics.add(log2);
            }
            order.put("logistics", logistics);

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取订单详情失败\"}");
        }
    }

    // 支付订单
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id, @RequestBody Map<String, Object> paymentData, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"订单不存在\"}");
            }

            // 验证用户是否是订单的买家
            if (!trade.getBuyerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限支付该订单\"}");
            }

            // 验证订单状态
            if (!"pending".equals(trade.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"订单状态不正确，无法支付\"}");
            }

            // 处理支付方式
            if (paymentData.containsKey("paymentMethod")) {
                String paymentMethod = (String) paymentData.get("paymentMethod");
                // 这里可以添加支付方式的处理逻辑
                logger.info("订单 {} 使用 {} 支付", id, paymentMethod);
            }

            // 更新订单状态为已支付
            trade.setStatus("paid");
            Trade updatedTrade = tradeRepository.save(trade);

            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            logger.error("支付订单失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"支付订单失败\"}");
        }
    }

    // 取消订单
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"订单不存在\"}");
            }

            // 验证用户是否是订单的买家或卖家
            if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限取消该订单\"}");
            }

            // 验证订单状态
            if (!"pending".equals(trade.getStatus()) && !"paid".equals(trade.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"订单状态不正确，无法取消\"}");
            }

            // 更新订单状态为已取消
            trade.setStatus("cancelled");
            Trade updatedTrade = tradeRepository.save(trade);

            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            logger.error("取消订单失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"取消订单失败\"}");
        }
    }

    // 确认收货
    @PutMapping("/{id}/receive")
    public ResponseEntity<?> receiveOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"订单不存在\"}");
            }

            // 验证用户是否是订单的买家
            if (!trade.getBuyerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限确认收货\"}");
            }

            // 验证订单状态
            if (!"shipped".equals(trade.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"订单状态不正确，无法确认收货\"}");
            }

            // 更新订单状态为已完成
            trade.setStatus("completed");
            Trade updatedTrade = tradeRepository.save(trade);

            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            logger.error("确认收货失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"确认收货失败\"}");
        }
    }

    // 卖家发货
    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"订单不存在\"}");
            }

            // 验证用户是否是订单的卖家
            if (!trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限发货\"}");
            }

            // 验证订单状态
            if (!"paid".equals(trade.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"订单状态不正确，无法发货\"}");
            }

            // 更新订单状态为已发货
            trade.setStatus("shipped");
            Trade updatedTrade = tradeRepository.save(trade);

            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            logger.error("发货失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"发货失败\"}");
        }
    }
}
