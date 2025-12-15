package com.pe.inventoryapp.backend.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.user.model.entity.UserToken;

import jakarta.transaction.Transactional;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
  Optional<UserToken> findByToken(String token);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserToken t WHERE t.expirationTime <= :now")
  void deleteAllExpiredTokens(LocalDateTime now);

  void deleteByToken(String token);
}
