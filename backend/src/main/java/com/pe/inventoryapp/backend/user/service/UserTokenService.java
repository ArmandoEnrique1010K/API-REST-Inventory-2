package com.pe.inventoryapp.backend.user.service;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.entity.UserToken;

public interface UserTokenService {
  boolean isExpired(String value);

  UserToken createTokenForUserByEmail(String email);

  boolean isTokenValid(String value);

  User findUserByToken(String token);
}
