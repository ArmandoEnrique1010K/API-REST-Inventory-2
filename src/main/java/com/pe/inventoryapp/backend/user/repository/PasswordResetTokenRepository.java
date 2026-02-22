package com.pe.inventoryapp.backend.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	Optional<PasswordResetToken> findByTokenHash(String tokenHash);

	@Modifying
	@Transactional
	@Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt <= :now OR t.used = true")
	int deleteAllExpiredOrUsedTokens(LocalDateTime now);
}
