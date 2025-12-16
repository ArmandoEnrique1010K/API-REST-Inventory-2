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
  // Recomendado utilizar boolean en lugar de Boolean
  // No definir nombres de campos que empiezan con "is" o "has"
  private boolean operator;
  private boolean secretary;
  private boolean admin;
}
