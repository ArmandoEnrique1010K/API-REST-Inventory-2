package com.pe.inventoryapp.backend.auth.model.request;

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
public class ValidateTokenRequest {
  @NotBlank(message = "El token es obligatorio y no debe ser nulo o vacío")
  @Pattern(regexp = "^[0-9]{6}$", message = "El token debe contener exactamente 6 digitos")
  private String value;
  
  private String requestId;
}
