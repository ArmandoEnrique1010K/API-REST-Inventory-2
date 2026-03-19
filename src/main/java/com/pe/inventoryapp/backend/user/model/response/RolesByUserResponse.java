package com.pe.inventoryapp.backend.user.model.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RolesByUserResponse {
  @NotNull
  private Boolean operator;
  @NotNull
  private Boolean secretary;
  @NotNull
  private Boolean admin;
}
