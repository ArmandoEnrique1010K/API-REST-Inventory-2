package com.pe.inventoryapp.backend.location.service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.repository.SubregionRepository;

public class SubregionDomainService {
  private final SubregionRepository subregionRepository;

  public SubregionDomainService(SubregionRepository subregionRepository) {
    this.subregionRepository = subregionRepository;
  }

  public void verifySubregionNameAvailableByRegionId(String name, Long regionId) {
    if (subregionRepository.existsByNameAndRegionId(name, regionId)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifySubregionNameAvailableByRegionIdExcludingId(String name, Long regionId, Long id) {
    if (subregionRepository.existsByNameAndRegionIdAndIdNot(name, regionId, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }

}
