package com.example.clothing.repository;

import com.example.clothing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查询（用于登录）
    User findByUsername(String username);

}
