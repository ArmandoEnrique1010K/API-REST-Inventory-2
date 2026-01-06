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
  // Recomendado utilizar boolean en lugar de Boolean
  // No definir nombres de campos que empiezan con "is" o "has"
  @NotNull
  private Boolean operator;
  @NotNull
  private Boolean secretary;
  @NotNull
  private Boolean admin;
}
