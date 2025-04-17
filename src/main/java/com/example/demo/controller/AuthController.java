package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // ใช้ final เพราะว่าค่าไม่เปลี่ยน และ IDE มันชอบ warning
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        try {
            // เอา req มาแยกใส่ตัวแปรก่อนจะได้อ่านง่าย
            String name = req.get("name");
            String email = req.get("email");
            String password = req.get("password");

            // เช็คข้อมูลก่อนส่งไป service
            if (name == null || email == null || password == null) {
                throw new RuntimeException("กรุณากรอกข้อมูลให้ครบ");
            }

            User user = userService.registerUser(name, email, password);

            // ส่ง response กลับ
            Map<String, Object> response = new HashMap<>();
            response.put("message", "ลงทะเบียนสำเร็จ");
            response.put("userId", user.getId());
            return response;

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return response;
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String password = req.get("password");

            if (email == null || password == null) {
                throw new RuntimeException("กรุณากรอก email และ password");
            }

            String token = userService.authenticateUser(email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return response;

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "email หรือ password ไม่ถูกต้อง");
            return response;
        }
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        try {
            // ดึง user จาก token ที่ส่งมา
            User user = userService.getCurrentUser();

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            return response;

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "ไม่พบข้อมูลผู้ใช้");
            return response;
        }
    }
}
