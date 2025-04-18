package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@NoArgsConstructor  
@Entity
@Table(name = "users")  
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
