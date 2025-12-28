package com.pe.inventoryapp.backend.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  Page<ListUsersResponse> findAllUsersByParams(String name, List<Long> roleIds,  Pageable pageable);

  DetailUserResponse findUserById(Long id);

  void updateUserProfileById(Long id, ProfileRequest profileRequest);

  void updateUserRolesById(Long id, RolesRequest rolesRequest);

  void deleteUserById(Long id);
}
