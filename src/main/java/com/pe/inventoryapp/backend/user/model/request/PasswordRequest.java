package com.pe.inventoryapp.backend.user.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PasswordRequest {
  @NotBlank(message = "Introduzca su contraseña actual")
  private String currentPassword;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]+$", message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número, sin caracteres especiales")
  private String newPassword;

  @NotBlank(message = "La contraseña es obligatoria")
  private String confirmPassword;

}
