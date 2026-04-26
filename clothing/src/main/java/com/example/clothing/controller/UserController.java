package com.example.clothing.controller;

import com.example.clothing.entity.User;
import com.example.clothing.repository.UserRepository;
import com.example.clothing.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private JwtUtils jwtUtils;

    // 登录
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody User user){

        User db = repository.findByUsername(user.getUsername());

        if(db != null && db.getPassword().equals(user.getPassword())){
            String token = jwtUtils.generateToken(db.getUsername());
            return Map.of("token", token, "role", db.getRole(), "id", db.getId(), "username", db.getUsername());
        }

        return Map.of("error","用户名或密码错误");
    }

    // ✅ 注册
    @PostMapping("/register")
    public Map<String,Object> register(@RequestBody User user){

        // 判断用户名是否已存在
        User exist = repository.findByUsername(user.getUsername());
        if(exist != null){
            return Map.of("error","用户名已存在");
        }

        // 默认角色 USER
        user.setRole("USER");

        repository.save(user);

        return Map.of("message","注册成功");
    }

    // 获取用户信息
    @GetMapping("/info")
    public Map<String,Object> getUserInfo(jakarta.servlet.http.HttpServletRequest request){
        // 从请求中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        if(userId == null){
            return Map.of("error","用户未登录");
        }
        
        // 根据用户ID获取用户信息
        User user = repository.findById(userId).orElse(null);
        if(user == null){
            return Map.of("error","用户不存在");
        }
        
        return Map.of("username", user.getUsername(), "role", user.getRole());
    }
}