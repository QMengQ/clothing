package com.example.clothing.repository;

import com.example.clothing.entity.Clothing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingRepository extends JpaRepository<Clothing, Long> {

    // 查询当前用户的衣物
    List<Clothing> findByUserId(Long userId);

}