package com.pe.inventoryapp.backend.user.model.request;

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
  @NotNull
  private Boolean operator;
  @NotNull
  private Boolean secretary;
  @NotNull
  private Boolean admin;
}
