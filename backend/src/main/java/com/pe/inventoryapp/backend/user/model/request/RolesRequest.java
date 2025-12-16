package com.pe.inventoryapp.backend.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesRequest {
  // Recomendado utilizar Boolean en lugar de boolean
  private Boolean isOperator;
  private Boolean isSecretary;
  private Boolean isAdmin;
}
