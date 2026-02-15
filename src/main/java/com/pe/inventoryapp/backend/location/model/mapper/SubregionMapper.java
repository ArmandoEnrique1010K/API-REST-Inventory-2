package com.pe.inventoryapp.backend.location.model.mapper;

import com.pe.inventoryapp.backend.location.model.entity.Subregion;
import com.pe.inventoryapp.backend.location.model.response.SubregionResponse;

public class SubregionMapper {
  private Subregion subregion;

  private SubregionMapper() {

  }

  public static SubregionMapper builder() {
    return new SubregionMapper();
  }

  public SubregionMapper setSubregion(Subregion subregion) {
    this.subregion = subregion;
    return this;
  }

  public SubregionResponse buildSubregionResponse() {

    if (subregion == null) {
      throw new RuntimeException("Debe pasar la entidad subregion");
    }
    return new SubregionResponse(
        subregion.getId(),
        subregion.getName(),
        subregion.getRegion().getId(),
        subregion.getRegion().getName());
  }
}
