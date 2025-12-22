package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;

public interface AuthService {
  Long findUserIdByEmail(String email);

  void updateUserPassword(String token, ChangePasswordRequest changePasswordRequest);

  void processUserForgotPassword(String email);

  void validateAndActivateResetToken(String token);
}
