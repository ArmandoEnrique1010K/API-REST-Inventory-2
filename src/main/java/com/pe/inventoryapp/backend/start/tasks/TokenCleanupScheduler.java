package com.pe.inventoryapp.backend.start.tasks;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pe.inventoryapp.backend.user.repository.PasswordResetTokenRepository;
import com.pe.inventoryapp.backend.user.repository.UserPasswordOtpRepository;

// Configuación de la ejecución de tareas programadas con relación al token de 6 digitos de reestablecimiento de contraseña
@Component
public class TokenCleanupScheduler {

  private final Logger log = Logger.getLogger(TokenCleanupScheduler.class.getName());

  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final UserPasswordOtpRepository userPasswordOtpRepository;

  public TokenCleanupScheduler(PasswordResetTokenRepository passwordResetTokenRepository, UserPasswordOtpRepository userPasswordOtpRepository) {
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.userPasswordOtpRepository = userPasswordOtpRepository;
  }

  // Configuración de cron
  // Esta tarea se ejecuta cada 10 minutos
  @Scheduled(cron = "0 0/10 * * * *")
  public void deleteExpiredTokens() {
    int userPasswordOtpDeleted = userPasswordOtpRepository.deleteAllExpiredOrVerifiedTokens(LocalDateTime.now());
    log.info("Número de UserPasswordOtp eliminados: " + userPasswordOtpDeleted);

    int passwordResetTokensDeleted = passwordResetTokenRepository.deleteAllExpiredOrUsedTokens(LocalDateTime.now());
    log.info("Número de PasswordResetToken eliminados: " + passwordResetTokensDeleted);    
  }
}
