package com.pe.inventoryapp.backend.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
  @NotBlank(message = "El nombre es obligatorio")
  private String firstname;

  @NotBlank(message = "El apellido es obligatorio")
  private String lastname;

  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene el formato adecuado")
  private String email;

  @NotNull(message = "El DNI es obligatorio")
  @Min(value = 10000000, message = "El valor no pasa del limite establecido")
  @Max(value = 99999999, message = "El valor sobrepasa del limite establecido")
  private Integer dni;
}
