package com.pe.inventoryapp.backend.stocklot.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {
  @NotBlank(message = "Introduzca el nombre de una empresa")
  private String name;
}
