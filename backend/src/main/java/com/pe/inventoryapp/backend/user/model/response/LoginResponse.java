package com.pe.inventoryapp.backend.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor

public class LoginResponse {
  private String type;
  private String token;
  private String message;
}