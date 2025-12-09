package com.pe.inventoryapp.backend.auth.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ChangePasswordRequest {
  @NotBlank(message = "Introduzca su nueva contraseña")
  private String newPassword;

}
