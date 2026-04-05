package com.pe.inventoryapp.backend.summary.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionSummaryDTO {

  private Long regionId;
  private String regionName;
  private Long totalQuantity;

  private List<SubregionSummaryDTO> subregions;
}