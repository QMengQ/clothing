package com.example.clothing.controller;

import com.example.clothing.entity.Trade;
import com.example.clothing.entity.TradeClothing;
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
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trade")
@CrossOrigin
public class TradeController {

    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeClothingRepository tradeClothingRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取服装列表（仅显示审核通过的服装）
    @GetMapping("/clothing")
    public ResponseEntity<?> getClothingList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int sizePerPage) {
        try {
            // 获取所有审核通过的服装
            List<TradeClothing> clothingList = tradeClothingRepository.findAll();
            // 过滤出审核通过的服装
            List<TradeClothing> approvedList = clothingList.stream()
                    .filter(clothing -> "approved".equals(clothing.getReviewStatus()))
                    .toList();
            
            // 应用分页
            int total = approvedList.size();
            int start = (page - 1) * sizePerPage;
            int end = Math.min(start + sizePerPage, total);
            List<TradeClothing> paginatedList = new java.util.ArrayList<>();
            if (start < total) {
                paginatedList = approvedList.subList(start, end);
            }
            
            // 构建响应，包含用户名信息
            java.util.List<java.util.Map<String, Object>> clothingWithUserInfo = new java.util.ArrayList<>();
            for (TradeClothing clothing : paginatedList) {
                java.util.Map<String, Object> clothingMap = new java.util.HashMap<>();
                clothingMap.put("id", clothing.getId());
                clothingMap.put("title", clothing.getTitle());
                clothingMap.put("category", clothing.getCategory());
                clothingMap.put("size", clothing.getSize());
                clothingMap.put("clothingCondition", clothing.getClothingCondition());
                clothingMap.put("price", clothing.getPrice());
                clothingMap.put("rentalPrice", clothing.getRentalPrice());
                clothingMap.put("description", clothing.getDescription());
                clothingMap.put("imageUrls", clothing.getImageUrls());
                clothingMap.put("userId", clothing.getUserId());
                clothingMap.put("status", clothing.getStatus());
                clothingMap.put("createdAt", clothing.getCreatedAt());
                clothingMap.put("updatedAt", clothing.getUpdatedAt());
                
                // 获取用户名
                if (clothing.getUserId() != null) {
                    com.example.clothing.entity.User user = userRepository.findById(clothing.getUserId()).orElse(null);
                    if (user != null) {
                        clothingMap.put("username", user.getUsername());
                    }
                }
                
                clothingWithUserInfo.add(clothingMap);
            }
            
            // 构建响应
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", clothingWithUserInfo);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取服装列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取服装列表失败\"}");
        }
    }

    // 获取服装详情
    @GetMapping("/clothing/{id}")
    public ResponseEntity<?> getClothingById(@PathVariable Long id) {
        try {
            TradeClothing clothing = tradeClothingRepository.findById(id).orElse(null);
            if (clothing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"服装不存在\"}");
            }
            return ResponseEntity.ok(clothing);
        } catch (Exception e) {
            logger.error("获取服装详情失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取服装详情失败\"}");
        }
    }

    // 删除服装
    @DeleteMapping("/clothing/{id}")
    public ResponseEntity<?> deleteClothing(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            TradeClothing clothing = tradeClothingRepository.findById(id).orElse(null);
            if (clothing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"服装不存在\"}");
            }
            
            // 检查是否是服装的所有者
            if (!clothing.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限删除该服装\"}");
            }
            
            tradeClothingRepository.delete(clothing);
            return ResponseEntity.ok("{\"message\": \"服装删除成功\"}");
        } catch (Exception e) {
            logger.error("删除服装失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"删除服装失败\"}");
        }
    }

    // 添加服装
    @PostMapping("/clothing")
    public ResponseEntity<?> addClothing(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String size,
            @RequestParam String status,
            @RequestParam Double price,
            @RequestParam(required = false) Double rentalPrice,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) String imageUrls,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            // 创建新服装
            TradeClothing clothing = new TradeClothing();
            clothing.setTitle(title);
            clothing.setCategory(category);
            clothing.setSize(size);
            clothing.setClothingCondition(status);
            clothing.setPrice(price);
            clothing.setRentalPrice(rentalPrice);
            clothing.setDescription(description);
            clothing.setUserId(userId);
            clothing.setStatus("available");
            
            // 处理图片上传
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // 使用传递过来的图片URL
                clothing.setImageUrls(imageUrls);
            } else if (images != null && images.length > 0) {
                // 处理上传的图片
                StringBuilder newImageUrls = new StringBuilder();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(image.getOriginalFilename());
                        // 使用绝对路径，确保目录存在
                        String uploadDir = System.getProperty("user.dir") + "/uploads";
                        File dir = new File(uploadDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File dest = new File(uploadDir, fileName);
                        image.transferTo(dest);
                        if (newImageUrls.length() > 0) {
                            newImageUrls.append(",");
                        }
                        newImageUrls.append(fileName);
                    }
                }
                clothing.setImageUrls(newImageUrls.toString());
            }
            
            // 保存服装
            TradeClothing savedClothing = tradeClothingRepository.save(clothing);
            return ResponseEntity.ok(savedClothing);
        } catch (Exception e) {
            logger.error("添加服装失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"添加服装失败\"}");
        }
    }

    // 创建交易
    @PostMapping
    public ResponseEntity<?> createTrade(@RequestBody Trade trade, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            // 设置买家ID
            trade.setBuyerId(userId);
            // 设置卖家ID（暂时设为1，后续需要从服装信息中获取）
            trade.setSellerId(1L);
            // 设置交易状态
            trade.setStatus("pending");
            // 确保价格不为空
            if (trade.getPrice() == null) {
                trade.setPrice(java.math.BigDecimal.ZERO);
            }
            
            Trade createdTrade = tradeRepository.save(trade);
            return ResponseEntity.ok(createdTrade);
        } catch (Exception e) {
            logger.error("创建交易失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"创建交易失败\"}");
        }
    }

    // 获取交易列表
    @GetMapping
    public ResponseEntity<?> getTradeList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            List<Trade> tradeList = tradeRepository.findByBuyerIdOrSellerId(userId, userId);
            return ResponseEntity.ok(tradeList);
        } catch (Exception e) {
            logger.error("获取交易列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取交易列表失败\"}");
        }
    }

    // 获取交易详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getTradeById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"交易不存在\"}");
            }
            
            // 验证用户是否有权限查看该交易
            if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限查看该交易\"}");
            }
            
            return ResponseEntity.ok(trade);
        } catch (Exception e) {
            logger.error("获取交易详情失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取交易详情失败\"}");
        }
    }

    // 更新交易状态
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTradeStatus(@PathVariable Long id, @RequestParam String status, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"交易不存在\"}");
            }
            
            // 验证用户是否有权限更新该交易
            if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限更新该交易\"}");
            }
            
            trade.setStatus(status);
            Trade updatedTrade = tradeRepository.save(trade);
            return ResponseEntity.ok(updatedTrade);
        } catch (Exception e) {
            logger.error("更新交易状态失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"更新交易状态失败\"}");
        }
    }

    // 确认交易完成
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeTrade(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            Trade trade = tradeRepository.findById(id).orElse(null);
            if (trade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"交易不存在\"}");
            }
            
            // 验证用户是否有权限完成该交易
            if (!trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限完成该交易\"}");
            }
            
            trade.setStatus("completed");
            Trade completedTrade = tradeRepository.save(trade);
            return ResponseEntity.ok(completedTrade);
        } catch (Exception e) {
            logger.error("完成交易失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"完成交易失败\"}");
        }
    }
    
    // 获取待审核服装列表（管理员接口）
    @GetMapping("/admin/clothing/pending")
    public ResponseEntity<?> getPendingClothingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int sizePerPage) {
        try {
            // 获取所有待审核的服装
            List<TradeClothing> clothingList = tradeClothingRepository.findAll();
            List<TradeClothing> pendingList = clothingList.stream()
                    .filter(clothing -> "pending".equals(clothing.getReviewStatus()))
                    .toList();
            
            // 应用分页
            int total = pendingList.size();
            int start = (page - 1) * sizePerPage;
            int end = Math.min(start + sizePerPage, total);
            List<TradeClothing> paginatedList = new java.util.ArrayList<>();
            if (start < total) {
                paginatedList = pendingList.subList(start, end);
            }
            
            // 构建响应，包含用户名信息
            java.util.List<java.util.Map<String, Object>> clothingWithUserInfo = new java.util.ArrayList<>();
            for (TradeClothing clothing : paginatedList) {
                java.util.Map<String, Object> clothingMap = new java.util.HashMap<>();
                clothingMap.put("id", clothing.getId());
                clothingMap.put("title", clothing.getTitle());
                clothingMap.put("category", clothing.getCategory());
                clothingMap.put("size", clothing.getSize());
                clothingMap.put("clothingCondition", clothing.getClothingCondition());
                clothingMap.put("price", clothing.getPrice());
                clothingMap.put("rentalPrice", clothing.getRentalPrice());
                clothingMap.put("description", clothing.getDescription());
                clothingMap.put("imageUrls", clothing.getImageUrls());
                clothingMap.put("userId", clothing.getUserId());
                clothingMap.put("status", clothing.getStatus());
                clothingMap.put("reviewStatus", clothing.getReviewStatus());
                clothingMap.put("createdAt", clothing.getCreatedAt());
                clothingMap.put("updatedAt", clothing.getUpdatedAt());
                
                // 获取用户名
                if (clothing.getUserId() != null) {
                    com.example.clothing.entity.User user = userRepository.findById(clothing.getUserId()).orElse(null);
                    if (user != null) {
                        clothingMap.put("username", user.getUsername());
                    }
                }
                
                clothingWithUserInfo.add(clothingMap);
            }
            
            // 构建响应
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", clothingWithUserInfo);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取待审核服装列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取待审核服装列表失败\"}");
        }
    }
    
    // 批准服装（管理员接口）
    @PutMapping("/admin/clothing/{id}/approve")
    public ResponseEntity<?> approveClothing(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long reviewerId = (Long) request.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"管理员未登录\"}");
            }
            
            TradeClothing clothing = tradeClothingRepository.findById(id).orElse(null);
            if (clothing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"服装不存在\"}");
            }
            
            // 更新审核状态
            clothing.setReviewStatus("approved");
            clothing.setReviewerId(reviewerId);
            clothing.setReviewDate(new java.sql.Date(System.currentTimeMillis()));
            clothing.setRejectionReason(null);
            
            TradeClothing updatedClothing = tradeClothingRepository.save(clothing);
            return ResponseEntity.ok(updatedClothing);
        } catch (Exception e) {
            logger.error("批准服装失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"批准服装失败\"}");
        }
    }
    
    // 拒绝服装（管理员接口）
    @PutMapping("/admin/clothing/{id}/reject")
    public ResponseEntity<?> rejectClothing(
            @PathVariable Long id, 
            @RequestParam String rejectionReason, 
            HttpServletRequest request) {
        try {
            Long reviewerId = (Long) request.getAttribute("userId");
            if (reviewerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"管理员未登录\"}");
            }
            
            if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"拒绝原因不能为空\"}");
            }
            
            TradeClothing clothing = tradeClothingRepository.findById(id).orElse(null);
            if (clothing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"服装不存在\"}");
            }
            
            // 更新审核状态
            clothing.setReviewStatus("rejected");
            clothing.setReviewerId(reviewerId);
            clothing.setReviewDate(new java.sql.Date(System.currentTimeMillis()));
            clothing.setRejectionReason(rejectionReason);
            
            TradeClothing updatedClothing = tradeClothingRepository.save(clothing);
            return ResponseEntity.ok(updatedClothing);
        } catch (Exception e) {
            logger.error("拒绝服装失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"拒绝服装失败\"}");
        }
    }
    
    // 获取用户自己的服装列表（包括审核状态）
    @GetMapping("/user/clothing")
    public ResponseEntity<?> getUserClothingList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            // 获取用户所有服装
            List<TradeClothing> clothingList = tradeClothingRepository.findAll();
            List<TradeClothing> userList = clothingList.stream()
                    .filter(clothing -> userId.equals(clothing.getUserId()))
                    .toList();
            
            // 构建响应
            java.util.List<java.util.Map<String, Object>> responseList = new java.util.ArrayList<>();
            for (TradeClothing clothing : userList) {
                java.util.Map<String, Object> clothingMap = new java.util.HashMap<>();
                clothingMap.put("id", clothing.getId());
                clothingMap.put("title", clothing.getTitle());
                clothingMap.put("category", clothing.getCategory());
                clothingMap.put("size", clothing.getSize());
                clothingMap.put("clothingCondition", clothing.getClothingCondition());
                clothingMap.put("price", clothing.getPrice());
                clothingMap.put("rentalPrice", clothing.getRentalPrice());
                clothingMap.put("description", clothing.getDescription());
                clothingMap.put("imageUrls", clothing.getImageUrls());
                clothingMap.put("status", clothing.getStatus());
                clothingMap.put("reviewStatus", clothing.getReviewStatus());
                clothingMap.put("rejectionReason", clothing.getRejectionReason());
                clothingMap.put("createdAt", clothing.getCreatedAt());
                clothingMap.put("updatedAt", clothing.getUpdatedAt());
                
                responseList.add(clothingMap);
            }
            
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            logger.error("获取用户服装列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取用户服装列表失败\"}");
        }
    }
    
    // 重新提交服装审核
    @PutMapping("/clothing/{id}/resubmit")
    public ResponseEntity<?> resubmitClothing(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            TradeClothing clothing = tradeClothingRepository.findById(id).orElse(null);
            if (clothing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"服装不存在\"}");
            }
            
            // 验证用户是否是服装所有者
            if (!userId.equals(clothing.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"无权限重新提交该服装\"}");
            }
            
            // 只有被拒绝的服装才能重新提交
            if (!"rejected".equals(clothing.getReviewStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"只有被拒绝的服装才能重新提交\"}");
            }
            
            // 更新审核状态为待审核
            clothing.setReviewStatus("pending");
            clothing.setReviewerId(null);
            clothing.setReviewDate(null);
            clothing.setRejectionReason(null);
            
            TradeClothing updatedClothing = tradeClothingRepository.save(clothing);
            return ResponseEntity.ok(updatedClothing);
        } catch (Exception e) {
            logger.error("重新提交服装失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"重新提交服装失败\"}");
        }
    }
}
