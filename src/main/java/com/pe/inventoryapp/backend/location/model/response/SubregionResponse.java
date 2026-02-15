package com.pe.inventoryapp.backend.location.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubregionResponse {
  private Long id;
  private String name;

  private Long regionId;
  private String regionName;
}
