package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  List<ListUsersResponse> findAll();

  Optional<DetailUserResponse> findUserById(Long id);

  String updateProfile(Long id, ProfileRequest profileRequest);

  String updatePassword(Long id, PasswordRequest passwordRequest);

  void remove(Long id);

  void verifyUserEmailExists(String email);
}
