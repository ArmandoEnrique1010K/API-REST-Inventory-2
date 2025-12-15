package com.pe.inventoryapp.backend.user.service;

public interface UserTokenService {
  boolean isExpired(String token);

  String generateTokenForUserByEmail(String email);

  boolean isTokenValid(String token);

  Long findUserIdByUserToken(String token);

  void invalidateToken(String token);
}
