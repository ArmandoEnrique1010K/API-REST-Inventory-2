package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  Long findUserIdByEmail(String email);

  void updateUserPassword(String token, ChangePasswordRequest changePasswordRequest);

  void processUserForgotPassword(String email);

  void validateAndActivateResetToken(String token);

  void logout(HttpServletResponse response);
}
