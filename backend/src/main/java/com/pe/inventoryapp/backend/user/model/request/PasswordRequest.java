package com.pe.inventoryapp.backend.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PasswordRequest {
  @NotBlank
  private String currentPassword;
  @NotBlank
  private String newPassword;
  @NotBlank
  private String confirmPassword;

}
