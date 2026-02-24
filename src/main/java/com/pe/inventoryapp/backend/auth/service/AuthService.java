package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ValidateTokenRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  Long findUserIdByEmail(String email);

  String processUserForgotPasswordAndReturnRequestId(String email);

  String validateAndActivateResetToken(ValidateTokenRequest validateTokenRequest);

  void updateUserPassword(ChangePasswordRequest changePasswordRequest);

  void logout(HttpServletResponse response);

  DetailUserResponse getCurrentSession(Long userId);
}
