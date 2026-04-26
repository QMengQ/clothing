package com.example.clothing.repository;

import com.example.clothing.entity.IdleClothingAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdleClothingAlertRepository extends JpaRepository<IdleClothingAlert, Long> {

    // 根据用户ID和状态查询预警
    List<IdleClothingAlert> findByUserIdAndStatus(Long userId, String status);

    // 根据衣物ID查询预警
    IdleClothingAlert findByClothingId(Long clothingId);

    // 根据用户ID查询所有预警
    List<IdleClothingAlert> findByUserId(Long userId);

    // 根据状态查询预警
    List<IdleClothingAlert> findByStatus(String status);

}
