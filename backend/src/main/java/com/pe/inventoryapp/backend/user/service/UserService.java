package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {
  String register(RegisterRequest registerRequest);

  List<ListUsersResponse> findAll();

  Optional<DetailUserResponse> findById(Long id);

  void remove(Long id);

  User findUserById(Long id);

  String updateProfile(Long id, ProfileRequest profileRequest);

  Boolean validatePassword(Long id, PasswordRequest passwordRequest);

  String updatePassword(Long id, PasswordRequest passwordRequest);
}
