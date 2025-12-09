package com.pe.inventoryapp.backend.auth.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessfulResponse {
  private String type;
  private String token;
  private String message;
}