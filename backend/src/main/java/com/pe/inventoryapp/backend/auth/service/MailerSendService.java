package com.pe.inventoryapp.backend.auth.service;

public interface MailerSendService {
  void sendResetPasswordToken(String toEmail, String token);
}
