package com.pe.inventoryapp.backend.auth.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginErrorResponse {
  private String type;
  private String message;
}
