package com.pe.inventoryapp.backend.summary.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubregionSummaryDTO {
  private Long subregionId;
  private String subregionName;
  private Long totalQuantity;
  private List<ModelProductSummaryDTO> items;
}