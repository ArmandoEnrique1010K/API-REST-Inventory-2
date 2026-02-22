package com.pe.inventoryapp.backend.auth.model.entity;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "password_reset_otp")
public class UserPasswordOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifica la solicitud (evita reutilización)
    @Column(nullable = false, length = 36)
    private String requestId;

    // Hash del OTP (NUNCA el OTP plano)
    @Column(nullable = false, length = 255)
    private String otpHash;

    @Column(nullable = false)
    private Integer attempts; // = 0;

    @Column(nullable = false)
    private boolean verified; // = false;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Relación fuerte: el ID no cambia
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
