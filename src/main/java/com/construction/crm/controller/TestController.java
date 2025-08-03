package com.construction.crm.controller;

import com.construction.crm.entity.User;
import com.construction.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Construction CRM System!");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @GetMapping("/users/count")
    public Map<String, Object> getUserCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User count endpoint");
        response.put("status", "success");
        response.put("userCount", userRepository.count());
        return response;
    }
    
    @GetMapping("/users/list")
    public Map<String, Object> getUsersList() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        response.put("message", "Users list");
        response.put("status", "success");
        response.put("userCount", users.size());
        response.put("users", users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("enabled", user.isEnabled());
            userMap.put("roles", user.getRoles().stream().map(role -> role.getName().name()).toList());
            return userMap;
        }).toList());
        return response;
    }
}