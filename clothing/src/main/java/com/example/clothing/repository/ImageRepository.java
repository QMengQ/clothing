package com.example.clothing.repository;

import com.example.clothing.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByEntityIdAndEntityType(Long entityId, String entityType);
    List<Image> findByUserId(Long userId);
}
