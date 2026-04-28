package com.example.clothing.controller;

import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.IdleClothingAlert;
import com.example.clothing.repository.ClothingRepository;
import com.example.clothing.repository.IdleClothingAlertRepository;
import com.example.clothing.service.IdleClothingAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/idle-alerts")
@CrossOrigin
public class IdleClothingAlertController {

    @Autowired
    private IdleClothingAlertService alertService;
    
    @Autowired
    private ClothingRepository clothingRepository;
    
    @Autowired
    private IdleClothingAlertRepository alertRepository;

    // 手动触发闲置衣物扫描
    @PostMapping("/scan")
    public String scanIdleClothing(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        alertService.scanIdleClothing(userId);
        return "{\"message\": \"扫描完成\"}";
    }

    // 获取用户的待处理预警
    @GetMapping("/pending")
    public List<IdleClothingAlert> getPendingAlerts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return alertService.getUserPendingAlerts(userId);
    }

    // 获取用户的所有预警
    @GetMapping("/all")
    public List<IdleClothingAlert> getAllAlerts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return alertService.getUserAlerts(userId);
    }

    // 标记预警为已处理
    @PutMapping("/{id}/process")
    public String markAsProcessed(@PathVariable Long id) {
        alertService.markAlertAsProcessed(id);
        return "{\"message\": \"预警已标记为已处理\"}";
    }

    // 标记预警为已忽略
    @PutMapping("/{id}/dismiss")
    public String markAsDismissed(@PathVariable Long id) {
        alertService.markAlertAsDismissed(id);
        return "{\"message\": \"预警已标记为已忽略\"}";
    }

    // 获取闲置衣物统计数据
    @GetMapping("/stats")
    public IdleClothingAlertService.IdleClothingStats getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return alertService.getIdleClothingStats(userId);
    }

    // 测试扫描功能（绕过JWT）
    @GetMapping("/test-scan/{userId}")
    public String testScan(@PathVariable Long userId) {
        alertService.scanIdleClothing(userId);
        return "{\"message\": \"扫描完成\"}";
    }

    // 测试创建预警记录
    @GetMapping("/test-create")
    public String testCreate() {
        alertService.createTestAlert();
        return "{\"message\": \"测试预警记录创建成功\"}";
    }

    // 为用户所有衣物创建预警（不检查闲置天数）
    @GetMapping("/force-scan/{userId}")
    public String forceScan(@PathVariable Long userId) {
        alertService.createAlertForAllClothing(userId);
        return "{\"message\": \"强制扫描完成\"}";
    }

    // 强制扫描用户4的所有衣物（绕过JWT）
    @GetMapping("/force-scan-user4")
    public String forceScanUser4() {
        alertService.createAlertForAllClothing(4L);
        return "{\"message\": \"强制扫描用户4完成\"}";
    }
    
    // 调试接口：获取用户所有衣物
    @GetMapping("/debug/user-clothing/{userId}")
    public List<Clothing> getUserClothing(@PathVariable Long userId) {
        return clothingRepository.findByUserId(userId);
    }
    
    // 调试接口：获取所有用户衣物
    @GetMapping("/debug/all-clothing")
    public List<Clothing> getAllClothing() {
        return clothingRepository.findAll();
    }

    // 调试接口：获取所有预警（所有用户）
    @GetMapping("/debug/all-alerts")
    public List<IdleClothingAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // 调试接口：获取指定用户的所有预警
    @GetMapping("/debug/user-alerts/{userId}")
    public List<IdleClothingAlert> getUserAlerts(@PathVariable Long userId) {
        return alertRepository.findByUserId(userId);
    }

    // 测试：为用户4添加测试闲置衣物
    @GetMapping("/debug/add-test-clothing")
    public String addTestClothing() {
        Clothing clothing = new Clothing();
        clothing.setName("测试闲置衣物(2025-02-04)");
        clothing.setType("上衣");
        clothing.setSize("L");
        clothing.setSeason("春季");
        clothing.setStatus("良好");
        clothing.setLocation("衣柜");
        clothing.setUserId(4L);
        // 设置最后穿着日期为2025-02-04，超过180天
        clothing.setLastWearDate(Date.valueOf("2025-02-04"));
        clothing.setPurchaseDate(Date.valueOf("2024-01-01"));
        clothing.setCategory("上衣");
        clothingRepository.save(clothing);
        return "{\"message\": \"测试衣物添加成功，ID: " + clothing.getId() + "\"}";
    }

    // 测试：扫描指定用户的闲置衣物
    @GetMapping("/debug/scan-user/{userId}")
    public String scanUser(@PathVariable Long userId) {
        alertService.scanIdleClothing(userId);
        return "{\"message\": \"用户" + userId + "扫描完成\"}";
    }

    // 测试：扫描用户4的闲置衣物
    @GetMapping("/debug/scan-user4")
    public String scanUser4() {
        alertService.scanIdleClothing(4L);
        return "{\"message\": \"用户4扫描完成\"}";
    }

    // 测试：获取用户4的所有预警
    @GetMapping("/debug/user4-alerts")
    public List<IdleClothingAlert> getUser4Alerts() {
        return alertRepository.findByUserId(4L);
    }
    
    // 调试接口：强制更新所有预警记录的季节信息
    @GetMapping("/debug/update-all-seasons")
    public String updateAllSeasons() {
        List<IdleClothingAlert> allAlerts = alertRepository.findAll();
        for (IdleClothingAlert alert : allAlerts) {
            Clothing clothing = clothingRepository.findById(alert.getClothingId()).orElse(null);
            if (clothing != null) {
                String season = clothing.getSeason() != null ? clothing.getSeason() : "无";
                alert.setExceptionRule(season);
                alertRepository.save(alert);
                System.out.println("更新预警记录: " + alert.getClothingName() + "，季节: " + season);
            }
        }
        return "{\"message\": \"所有预警记录季节信息已更新\"}";
    }
    
    // 调试接口：查看衣物表中的详细数据
    @GetMapping("/debug/clothing-details/{clothingId}")
    public Clothing getClothingDetails(@PathVariable Long clothingId) {
        return clothingRepository.findById(clothingId).orElse(null);
    }
}    
