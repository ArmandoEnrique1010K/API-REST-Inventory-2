package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;

public interface AuthService {
  Long findUserIdByEmail(String email);

  void updateUserPassword(String token, ChangePasswordRequest changePasswordRequest);

  void processForgotPassword(String email);

  void validateResetToken(String token);
}
