package com.example.clothing.controller;

import com.example.clothing.entity.Location;
import com.example.clothing.repository.LocationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@CrossOrigin
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @GetMapping
    public List<Location> list(HttpServletRequest request){
        Long userId=(Long)request.getAttribute("userId");
        return repository.findByUserId(userId);
    }

    @PostMapping
    public Location add(@RequestBody Location location,
                        HttpServletRequest request){

        Long userId=(Long)request.getAttribute("userId");
        location.setUserId(userId);

        return repository.save(location);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        repository.deleteById(id);
    }
} 