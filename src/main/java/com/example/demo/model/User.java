package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // ให้ lombok สร้าง getter/setter ให้หมด จะได้ไม่ต้องเขียนเอง
@NoArgsConstructor  // สร้าง constructor ว่างๆ ให้ JPA ใช้
@Entity
@Table(name = "users")  // ตั้งชื่อ table ให้เป็นพหูพจน์ตามมาตรฐาน
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  // ห้าม null
    private String name;

    @Column(nullable = false, unique = true)  // ห้าม null และห้ามซ้ำ
    private String email;

    @Column(nullable = false)
    private String password;  // เก็บ hash ของ password
}
