package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  List<ListUsersResponse> findAll();

  DetailUserResponse findUserById(Long id);

  void updateUserProfile(Long id, ProfileRequest profileRequest);

  void alterRoles(Long id, RolesRequest rolesRequest);

  void verifyUserEmailExists(String email);

  void remove(Long id);
}
