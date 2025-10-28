package com.pe.inventoryapp.backend.user.service;

import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.dto.UserResponse;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;

public interface UserService {
  String register(RegisterRequest registerRequest);

  Optional<UserResponse> findById(Long id);

  Optional<UserResponse> findByEmail(String email);

  Boolean getUserByEmail(String email);

  void remove(Long id);

  void verifyUser(String name);

}
