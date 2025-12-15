package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;

public interface AuthService {
  Long extractUserIdFromClaims(String header);

  User findUserById(Long id);

  DetailUserResponse findById(Long id);

  Long findUserIdByEmail(String email);

  boolean existsUserByEmail(String email);

  void changeUserPassword(String token, ChangePasswordRequest changePasswordRequest);

  void processForgotPassword(String email);

  public void validateResetToken(String token);
}
