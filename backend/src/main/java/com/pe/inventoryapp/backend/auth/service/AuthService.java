package com.pe.inventoryapp.backend.auth.service;

public interface AuthService {

  Long findIdByEmail(String email);

  void verifyUserEmailExists(String email);

  Long extracIdFromClaims(String header);

  boolean existsUserByEmail(String email);

  void changePassword(String password, Long id);
}
