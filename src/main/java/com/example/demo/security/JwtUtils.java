package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {

    // ดึงค่าจาก application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    // สร้าง token
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // เก็บ email ไว้ใน token
                .setIssuedAt(new Date())  // วันที่สร้าง
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))  // วันหมดอายุ
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))  // เซ็น token ด้วย secret key
                .compact();
    }

    // ดึง email จาก token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ตรวจสอบว่า token ถูกต้องไหม
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = getEmailFromToken(token);
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            // เช็คว่า email ตรงกับ user ที่ส่งมาไหม และ token หมดอายุยัง
            return email.equals(userDetails.getUsername()) && !expiration.before(new Date());
            
        } catch (Exception e) {
            // ถ้ามี error แสดงว่า token ไม่ถูกต้อง
            return false;
        }
    }
}
