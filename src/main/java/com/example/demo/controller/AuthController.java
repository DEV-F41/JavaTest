package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> req) {
        try {
            String name = req.get("name");
            String email = req.get("email");
            String password = req.get("password");

            if (name == null || email == null || password == null) {
                throw new RuntimeException("กรุณากรอกข้อมูลให้ครบ");
            }

            userService.registerUser(name, email, password);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return response;

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return response;
        }
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String password = req.get("password");

            if (email == null || password == null) {
                throw new RuntimeException("กรุณากรอก email และ password");
            }

            String token = userService.authenticateUser(email, password);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);
            return response;

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "email หรือ password ไม่ถูกต้อง");
            return response;
        }
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        try {
            User user = userService.getCurrentUser();

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ไม่พบข้อมูลผู้ใช้");
            return error;
        }
    }
}
