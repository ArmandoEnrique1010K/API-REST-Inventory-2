package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;

public interface AuthService {
  String register(RegisterRequest registerRequest);

  Long findIdByEmail(String email);

  void verifyUserEmailExists(String email);

  Long extracIdFromClaims(String header);

  String generateToken();

  public boolean existsUserByEmail(String email);

  public void sendResetPasswordToken(String toEmail, String token);
}
