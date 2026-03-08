package com.example.clothing.repository;

import com.example.clothing.entity.RecycleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecycleRepository extends JpaRepository<RecycleItem, Long> {

    // 查询当前用户的回收记录
    List<RecycleItem> findByUserId(Long userId);

    // 高级搜索方法
    @Query("SELECT r FROM RecycleItem r WHERE r.userId = :userId " +
           "AND (:clothingType IS NULL OR r.clothingType = :clothingType) " +
           "AND (:recycleType IS NULL OR r.recycleType = :recycleType) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:startDate IS NULL OR r.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR r.createdAt <= :endDate) " +
           "AND (:keyword IS NULL OR r.brand LIKE %:keyword% OR r.notes LIKE %:keyword%)")
    List<RecycleItem> searchByConditions(@Param("userId") Long userId,
                                         @Param("clothingType") String clothingType,
                                         @Param("recycleType") String recycleType,
                                         @Param("status") String status,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("keyword") String keyword);

}