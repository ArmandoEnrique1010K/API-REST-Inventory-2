package com.pe.inventoryapp.backend.user.model.request;

import com.pe.inventoryapp.backend.user.model.data.RoleName;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesRequest {
  // @NotNull
  // private Boolean user;
  // @NotNull
  // private Boolean operator;
  // @NotNull
  // private Boolean secretary;
  // @NotNull
  // private Boolean admin;
  
  @NotNull(message = "El rol es obligatorio")
  @Enumerated(EnumType.STRING)
  private RoleName role;
}
