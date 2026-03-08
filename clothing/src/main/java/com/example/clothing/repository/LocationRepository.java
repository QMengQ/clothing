package com.example.clothing.repository;

import com.example.clothing.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // 查询当前用户的收纳位置
    List<Location> findByUserId(Long userId);

}