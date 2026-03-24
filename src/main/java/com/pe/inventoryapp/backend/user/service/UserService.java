package com.pe.inventoryapp.backend.user.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersByRoleUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.model.response.RolesByUserResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  PageResponse<ListUsersResponse> findAllUsersByParams(String name, List<Long> roleIds,  Pageable pageable);

  List<ListUsersByRoleUserResponse> findFirstTenUsersByName(String name);

  DetailUserResponse findUserById(Long id);

  RolesByUserResponse getRolesByUser(Long idUser);

  void updateUserProfileById(Long id, ProfileRequest profileRequest);

  void updateUserRolesById(Long id, RolesRequest rolesRequest);

  void changeStatusUserById(Long id_user, Long id_authenticated_user);
}
