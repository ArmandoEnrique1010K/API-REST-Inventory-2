package com.pe.inventoryapp.backend.auth.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ForgotPasswordRequest {
  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene el formato adecuado")
  private String email;
}
