package com.pe.inventoryapp.backend.auth.model.request;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ForgotPasswordRequest {
  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene el formato adecuado")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|hotmail\\.com|outlook\\.com)$", message = "El correo debe pertenecer a Gmail, Hotmail u Outlook")
  private String email;
}