package com.pe.inventoryapp.backend.auth.service;

public interface MailerSendService {
  public void sendResetPasswordToken(String toEmail, String token);
}
