package com.pe.inventoryapp.backend.user.service;

import java.util.List;

import com.pe.inventoryapp.backend.user.model.response.RoleResponse;

public interface RoleService {
  List<RoleResponse> findAllRoles();

}
