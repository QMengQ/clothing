package com.example.clothing.controller;

import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.RecycleItem;
import com.example.clothing.entity.User;
import com.example.clothing.repository.ClothingRepository;
import com.example.clothing.repository.RecycleRepository;
import com.example.clothing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private RecycleRepository recycleRepository;

    // 用户管理
    @GetMapping("/users")
    public List<User> users(){
        return userRepository.findAll();
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
    }

    // 全部衣物
    @GetMapping("/clothing")
    public List<Clothing> allClothing(){
        return clothingRepository.findAll();
    }

    // 回收管理
    @GetMapping("/recycle")
    public List<RecycleItem> recycle(){
        return recycleRepository.findAll();
    }

    // 统计
    @GetMapping("/stats")
    public Map<String,Object> stats(){
        return Map.of(
                "userCount",userRepository.count(),
                "clothingCount",clothingRepository.count(),
                "recycleCount",recycleRepository.count()
        );
    }
    @GetMapping("/typeStats")
    public Map<String, Long> typeStats() {
        List<Clothing> list = clothingRepository.findAll();
        return list.stream().collect(Collectors.groupingBy(
                Clothing::getType,
                Collectors.counting()
        ));
    }

    @GetMapping("/recycleStats")
    public Map<String, Long> recycleStats() {
        List<RecycleItem> list = recycleRepository.findAll();
        return list.stream().collect(Collectors.groupingBy(
                RecycleItem::getStatus,
                Collectors.counting()
        ));
    }
}