package com.pe.inventoryapp.backend.summary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Este DTO representa un Objeto de transferencia de datos que sera utilizado en el repositorio SummaryRepository
public class SummaryDTO {
  private Long subregionId;
  private String subregionName;

  private Long regionId;
  private String regionName;

  private Long modelId;
  private String modelName;

  private Long productId;
  private String productName;

  // SI O SI DEBE SER UN LONG
  // En el repository, el metodo SUM devuelve un Long y no un Integer
  private Long totalQuantity;
}
