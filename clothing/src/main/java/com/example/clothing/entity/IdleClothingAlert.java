package com.example.clothing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
public class IdleClothingAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long clothingId;
    private Long userId;
    private Date lastWearDate;
    private Date alertDate;
    private Integer idleDays;
    private String status; // pending, processed, dismissed
    private String clothingName;
    private String clothingCategory;
    private String exceptionRule; // 例外规则，如季节性衣物

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClothingId() {
        return clothingId;
    }

    public void setClothingId(Long clothingId) {
        this.clothingId = clothingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getLastWearDate() {
        return lastWearDate;
    }

    public void setLastWearDate(Date lastWearDate) {
        this.lastWearDate = lastWearDate;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public Integer getIdleDays() {
        return idleDays;
    }

    public void setIdleDays(Integer idleDays) {
        this.idleDays = idleDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClothingName() {
        return clothingName;
    }

    public void setClothingName(String clothingName) {
        this.clothingName = clothingName;
    }

    public String getClothingCategory() {
        return clothingCategory;
    }

    public void setClothingCategory(String clothingCategory) {
        this.clothingCategory = clothingCategory;
    }

    public String getExceptionRule() {
        return exceptionRule;
    }

    public void setExceptionRule(String exceptionRule) {
        this.exceptionRule = exceptionRule;
    }
}
