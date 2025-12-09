package com.pe.inventoryapp.backend.test;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pe.inventoryapp.backend.user.repository.UserTokenRepository;

@Component
public class TokenCleanupScheduler {
  private final UserTokenRepository tokenRepository;

  public TokenCleanupScheduler(UserTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  // Se ejecuta cada minuto
  @Scheduled(cron = "0 * * * * *")
  public void deleteExpiredTokens() {
    System.out.println(LocalDateTime.now() + "Comienza el borrado de tokens expirados...");
    tokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
  }

}
