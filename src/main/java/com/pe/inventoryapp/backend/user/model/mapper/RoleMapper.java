package com.pe.inventoryapp.backend.user.model.mapper;

import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.response.RoleResponse;

public class RoleMapper {

  public static RoleResponse toResponse(Role role) {
    RoleResponse response = new RoleResponse();
    response.setId(role.getId());
    response.setLabel(role.getLabel());
    return response;
  }
}