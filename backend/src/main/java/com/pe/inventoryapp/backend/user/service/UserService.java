package com.pe.inventoryapp.backend.user.service;

import java.util.List;
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

  void updateUserRoles(Long id, RolesRequest rolesRequest);

  void deleteUser(Long id);
}
