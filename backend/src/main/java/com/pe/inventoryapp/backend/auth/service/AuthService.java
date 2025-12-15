package com.pe.inventoryapp.backend.auth.service;

import java.util.Optional;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;

public interface AuthService {
  Long extractIdUserFromClaims(String header);

  User findUserById(Long id);

  DetailUserResponse findById(Long id);

  Long findIdByEmail(String email);

  boolean existsUserByEmail(String email);

  void changePassword(String token, ChangePasswordRequest changePasswordRequest);
}
