package com.pe.inventoryapp.backend.auth.service;

import com.pe.inventoryapp.backend.auth.models.request.RegisterRequest;

public interface RegisterService {
  String register(RegisterRequest registerRequest);

  void verifyUserEmailExists(String email);
}
