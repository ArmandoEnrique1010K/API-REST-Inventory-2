package com.pe.inventoryapp.backend.auth.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class LoginRequest {
  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene el formato adecuado")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|hotmail\\.com|outlook\\.com)$", message = "El correo debe ser Gmail, Hotmail u Outlook")
  private String email;

  @NotBlank(message = "La contraseña es obligatoria")
  private String password;
}
