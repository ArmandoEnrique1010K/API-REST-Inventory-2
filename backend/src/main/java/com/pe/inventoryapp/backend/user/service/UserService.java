package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {

  List<ListUsersResponse> findAll();

  Optional<DetailUserResponse> findById(Long id);

  Optional<DetailUserResponse> findByEmail(String email);
  // Boolean getUserByEmail(String email);

  void remove(Long id);

  void verifyUser(String name);

  String updateProfile(Long id, ProfileRequest profileRequest);

  Boolean validatePassword(Long id, PasswordRequest passwordRequest);

  String updatePassword(Long id, PasswordRequest passwordRequest);
}
