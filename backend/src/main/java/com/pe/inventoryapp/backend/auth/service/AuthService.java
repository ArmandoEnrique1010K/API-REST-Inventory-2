package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.user.model.entity.User;

public interface AuthService {
  Long extracIdFromClaims(String header);

  User findUserById(Long id);

  Long findIdByEmail(String email);

  boolean existsUserByEmail(String email);

  void changePassword(String password, Long id);
}
