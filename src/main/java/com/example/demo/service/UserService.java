package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    
    public UserService(UserRepository userRepo, 
                      PasswordEncoder encoder,
                      AuthenticationManager authManager,
                      JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    public void registerUser(String name, String email, String password) {
        // เช็คก่อนว่ามี email นี้ในระบบยัง
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("email นี้มีในระบบแล้ว");
        }

        // สร้าง user ใหม่
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // เข้ารหัส password ก่อนเก็บลง DB
        user.setPassword(encoder.encode(password));

        userRepo.save(user);
    }

    public String authenticateUser(String email, String password) {
        try {
            // ให้ Spring จัดการเรื่อง authentication
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            // สร้าง token
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
            
            return jwtUtils.generateToken(userDetails);
            
        } catch (Exception e) {
            throw new RuntimeException("email หรือ password ไม่ถูกต้อง");
        }
    }

    // เพิ่มเมธอดสำหรับดึง user ปัจจุบัน
    public User getCurrentUser() {
        // ดึง email จาก token ที่ผ่านการ validate แล้ว
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // หา user จาก email
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้ใช้"));
    }

    // เผื่อต้องใช้ดึง user ด้วย email
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้ใช้"));
    }
}
