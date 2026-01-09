package com.pe.inventoryapp.backend.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListUsersByRoleUserResponse {
  private Long id;
  private String fullName;
  private String email;
  private Integer dni;
}
