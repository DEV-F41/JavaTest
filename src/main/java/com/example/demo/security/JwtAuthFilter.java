package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // ดึง token จาก header
            String token = getTokenFromHeader(request);
            
            // ถ้ามี token และยังไม่ได้ login
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // ดึง email จาก token
                String email = jwtUtils.getEmailFromToken(token);
                
                if (email != null) {
                    // ดึงข้อมูล user จาก email
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    // ตรวจสอบ token
                    if (jwtUtils.validateToken(token, userDetails)) {
                        // สร้าง authentication object
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // เซ็ต authentication ให้ Spring Security
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("เกิดข้อผิดพลาดในการตรวจสอบ token: " + e.getMessage());
        }

        // ส่งต่อไปที่ filter ถัดไป
        filterChain.doFilter(request, response);
    }

    // ดึง token จาก Authorization header
    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            // ตัด Bearer ออกเหลือแต่ token
            return header.substring(7);
        }
        
        return null;
    }
}
