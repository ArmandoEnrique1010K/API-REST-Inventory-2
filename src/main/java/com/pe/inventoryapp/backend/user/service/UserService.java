package com.pe.inventoryapp.backend.user.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.ListUsersByRoleUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.model.response.RolesByUserResponse;

public interface UserService {
  void registerUser(RegisterRequest registerRequest);

  PageResponse<ListUsersResponse> findAllUsersByParams(String keyword, RoleName role,  Pageable pageable);

  List<ListUsersByRoleUserResponse> findFirstTenUsersByName(String keyword);

  RolesByUserResponse getRolesByUser(Long idUser);

  void updateUserRolesById(Long id, RolesRequest rolesRequest);

  void changeStatusUserById(Long id_user, Long id_authenticated_user);
}
