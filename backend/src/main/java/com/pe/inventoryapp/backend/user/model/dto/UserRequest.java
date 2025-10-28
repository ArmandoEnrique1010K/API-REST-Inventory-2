package com.pe.inventoryapp.backend.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRequest {
  private Long id;
  private String firstname;
  private String lastname;
  private String email;

  private boolean isManager;
  private boolean isAdmin;
}
