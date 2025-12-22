package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  List<ListUsersResponse> findAllUsers();

  DetailUserResponse findUserById(Long id);

  void updateUserProfileById(Long id, ProfileRequest profileRequest);

  void updateUserRolesById(Long id, RolesRequest rolesRequest);

  void deleteUserById(Long id);
}
