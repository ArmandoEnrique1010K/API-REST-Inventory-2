package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.auth.model.entity.UserPasswordOtp;

import jakarta.transaction.Transactional;

public interface UserPasswordOtpRepository extends JpaRepository<UserPasswordOtp, Long> {
  Optional<UserPasswordOtp> findByRequestId(String requestId);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserPasswordOtp t WHERE t.expiresAt <= :now OR t.verified = true")
  int deleteAllExpiredOrVerifiedTokens(java.time.LocalDateTime now);
}
