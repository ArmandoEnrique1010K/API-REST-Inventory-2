package com.pe.inventoryapp.backend.security.config;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pe.inventoryapp.backend.user.repository.UserTokenRepository;

// Configuación de la ejecución de tareas programadas con relación al token de 6 digitos de reestablecimiento de contraseña
@Component
public class TokenCleanupScheduler {
  private final UserTokenRepository tokenRepository;

  public TokenCleanupScheduler(UserTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  // Configuración de cron
  // Esta tarea se ejecuta cada 5 minutos
  @Scheduled(cron = "0 0/5 * * * *")
  public void deleteExpiredTokens() {
    System.out.println(LocalDateTime.now() + ": Comienza el borrado de tokens expirados.");
    tokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
  }

}
