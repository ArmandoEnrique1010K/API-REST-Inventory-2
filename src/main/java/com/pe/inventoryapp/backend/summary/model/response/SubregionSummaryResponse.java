package com.pe.inventoryapp.backend.summary.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubregionSummaryResponse {
  private Long id;
  private String name;
  private Integer totalQuantity;

  private List<LocationSummaryResponse> locations;
}
