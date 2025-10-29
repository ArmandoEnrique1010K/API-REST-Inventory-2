package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.models.request.RegisterRequest;

public interface AuthService {
  String register(RegisterRequest registerRequest);

  Long findIdByEmail(String email);

  void verifyUserEmailExists(String email);
}
