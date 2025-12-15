package com.pe.inventoryapp.backend.auth.model.request;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ChangePasswordRequest {
  @NotBlank(message = "Introduzca su nueva contraseña")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]+$", message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número, sin caracteres especiales")
  private String newPassword;

  @NotBlank(message = "Confirme su nueva contraseña")
  private String confirmNewPassword;
}
