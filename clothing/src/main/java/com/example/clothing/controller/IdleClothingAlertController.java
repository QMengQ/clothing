package com.example.clothing.controller;

import com.example.clothing.entity.IdleClothingAlert;
import com.example.clothing.service.IdleClothingAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/idle-alerts")
@CrossOrigin
public class IdleClothingAlertController {

    @Autowired
    private IdleClothingAlertService alertService;

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
}    
