package com.pe.inventoryapp.backend.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "El nombre es obligatorio")
  private String firstname;

  @NotBlank(message = "El apellido es obligatorio")
  private String lastname;

  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene el formato adecuado")
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|hotmail\\.com|outlook\\.com)$", message = "El correo debe pertenecer a Gmail, Hotmail u Outlook")
  private String email;

  @NotNull(message = "El DNI es obligatorio")
  @Min(value = 10000000, message = "El valor no pasa del limite establecido")
  @Max(value = 99999999, message = "El valor sobrepasa del limite establecido")
  private Integer dni;

  @NotBlank(message = "La contraseña es obligatoria")
  private String password;

  @NotNull
  private Boolean operator;
  @NotNull
  private Boolean secretary;
  @NotNull
  private Boolean admin;
}