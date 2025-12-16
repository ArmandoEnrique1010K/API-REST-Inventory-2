package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;

public interface AuthService {

  DetailUserResponse findUserById(Long id);

  Long findUserIdByEmail(String email);

  void changeUserPassword(String token, ChangePasswordRequest changePasswordRequest);

  void processForgotPassword(String email);

  public void validateResetToken(String token);
}
