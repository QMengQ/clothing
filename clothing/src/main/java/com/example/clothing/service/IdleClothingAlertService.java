package com.example.clothing.service;

import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.IdleClothingAlert;
import com.example.clothing.repository.ClothingRepository;
import com.example.clothing.repository.IdleClothingAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdleClothingAlertService {

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private IdleClothingAlertRepository alertRepository;

    // 扫描闲置衣物并创建预警
    public void scanIdleClothing(Long userId) {
        System.out.println("开始扫描闲置衣物，用户ID: " + userId);
        List<Clothing> userClothing = clothingRepository.findByUserId(userId);
        System.out.println("用户衣物数量: " + userClothing.size());
        LocalDate today = LocalDate.now();

        for (Clothing clothing : userClothing) {
            System.out.println("检查衣物: " + clothing.getName() + "，最后穿着日期: " + clothing.getLastWearDate());
            // 检查衣物是否有最后穿着日期
            if (clothing.getLastWearDate() != null) {
                LocalDate lastWearDate = clothing.getLastWearDate().toLocalDate();
                long daysSinceLastWorn = ChronoUnit.DAYS.between(lastWearDate, today);
                System.out.println("闲置天数: " + daysSinceLastWorn);

                // 检查是否超过180天
                if (daysSinceLastWorn > 180) {
                    System.out.println("衣物已闲置超过180天: " + clothing.getName());
                    // 检查是否已经有预警记录
                    IdleClothingAlert existingAlert = alertRepository.findByClothingId(clothing.getId());
                    if (existingAlert == null) {
                        System.out.println("创建新的预警记录: " + clothing.getName());
                        // 创建新的预警记录
                        IdleClothingAlert alert = new IdleClothingAlert();
                        alert.setClothingId(clothing.getId());
                        alert.setUserId(clothing.getUserId());
                        alert.setLastWearDate(clothing.getLastWearDate());
                        alert.setAlertDate(Date.valueOf(today));
                        alert.setIdleDays((int) daysSinceLastWorn);
                        alert.setStatus("pending");
                        alert.setClothingName(clothing.getName());
                        alert.setClothingCategory(clothing.getCategory() != null ? clothing.getCategory() : "未分类");
                        alert.setExceptionRule(clothing.getSeason() != null ? clothing.getSeason() : "无"); // 使用季节作为例外规则

                        alertRepository.save(alert);
                        System.out.println("预警记录创建成功: " + clothing.getName());
                    } else if (existingAlert.getStatus().equals("processed")) {
                        System.out.println("更新预警状态: " + clothing.getName());
                        // 如果预警已处理，但又超过了180天，更新预警状态
                        existingAlert.setStatus("pending");
                        existingAlert.setAlertDate(Date.valueOf(today));
                        existingAlert.setIdleDays((int) daysSinceLastWorn);
                        alertRepository.save(existingAlert);
                        System.out.println("预警状态更新成功: " + clothing.getName());
                    } else {
                        System.out.println("预警记录已存在且状态为: " + existingAlert.getStatus());
                    }
                }
            } else {
                System.out.println("衣物没有最后穿着日期: " + clothing.getName());
            }
        }
        System.out.println("闲置衣物扫描完成");
    }

    // 获取用户的待处理预警
    public List<IdleClothingAlert> getUserPendingAlerts(Long userId) {
        return alertRepository.findByUserIdAndStatus(userId, "pending");
    }

    // 标记预警为已处理
    public void markAlertAsProcessed(Long alertId) {
        IdleClothingAlert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null) {
            alert.setStatus("processed");
            alertRepository.save(alert);
        }
    }

    // 标记预警为已忽略
    public void markAlertAsDismissed(Long alertId) {
        IdleClothingAlert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null) {
            alert.setStatus("dismissed");
            alertRepository.save(alert);
        }
    }

    // 获取用户的所有预警
    public List<IdleClothingAlert> getUserAlerts(Long userId) {
        return alertRepository.findByUserId(userId);
    }

    // 获取闲置衣物统计数据
    public IdleClothingStats getIdleClothingStats(Long userId) {
        List<Clothing> userClothing = clothingRepository.findByUserId(userId);
        List<IdleClothingAlert> userAlerts = alertRepository.findByUserId(userId);

        int totalClothing = userClothing.size();
        int idleClothing = (int) userAlerts.stream()
                .filter(alert -> alert.getStatus().equals("pending"))
                .count();

        // 按类别统计闲置衣物
        List<IdleClothingByCategory> categoryStats = userAlerts.stream()
                .filter(alert -> alert.getStatus().equals("pending"))
                .collect(Collectors.groupingBy(IdleClothingAlert::getClothingCategory, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new IdleClothingByCategory(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());

        return new IdleClothingStats(totalClothing, idleClothing, categoryStats);
    }

    // 内部类：闲置衣物统计
    public static class IdleClothingStats {
        private int totalClothing;
        private int idleClothing;
        private List<IdleClothingByCategory> categoryStats;

        public IdleClothingStats(int totalClothing, int idleClothing, List<IdleClothingByCategory> categoryStats) {
            this.totalClothing = totalClothing;
            this.idleClothing = idleClothing;
            this.categoryStats = categoryStats;
        }

        public int getTotalClothing() {
            return totalClothing;
        }

        public int getIdleClothing() {
            return idleClothing;
        }

        public List<IdleClothingByCategory> getCategoryStats() {
            return categoryStats;
        }
    }

    // 内部类：按类别统计的闲置衣物
    public static class IdleClothingByCategory {
        private String category;
        private int count;

        public IdleClothingByCategory(String category, int count) {
            this.category = category;
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public int getCount() {
            return count;
        }
    }

    // 测试方法：手动创建预警记录
    public void createTestAlert() {
        // 创建一个测试预警记录
        IdleClothingAlert alert = new IdleClothingAlert();
        alert.setClothingId(19L); // 使用id为19的衣物
        alert.setUserId(2L); // 使用id为2的用户
        alert.setLastWearDate(java.sql.Date.valueOf("2025-04-10"));
        alert.setAlertDate(java.sql.Date.valueOf(LocalDate.now()));
        alert.setIdleDays(365); // 闲置365天
        alert.setStatus("pending");
        alert.setClothingName("测试衣物");
        alert.setClothingCategory("上衣");
        alert.setExceptionRule("无");

        alertRepository.save(alert);
        System.out.println("测试预警记录创建成功");
    }
}    
