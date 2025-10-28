package com.pe.inventoryapp.backend.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "El nombre es obligatorio")
  private String firstname;

  @NotBlank(message = "El apellido es obligatorio")
  private String lastname;

  @NotBlank(message = "El correo es obligatorio")
  @Email
  private String email;

  @NotNull(message = "El DNI es obligatorio")
  private Integer dni;

  @NotBlank(message = "La contraseña es obligatoria")
  private String password;

  private boolean isManager;
  private boolean isAdmin;
}