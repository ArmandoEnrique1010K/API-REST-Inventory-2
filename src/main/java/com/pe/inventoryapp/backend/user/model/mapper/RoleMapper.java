 package com.pe.inventoryapp.backend.user.model.mapper;
 import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.response.RoleResponse;
 public class RoleMapper {
   public static RoleResponse toResponse(RoleName role) {
     RoleResponse response = new RoleResponse();
     // response.setId(role.getId());
     // response.setLabel(role.getLabel());

     response.setLabel(role.name());
     return response;
   }
 }