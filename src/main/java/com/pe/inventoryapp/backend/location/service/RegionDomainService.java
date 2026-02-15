package com.pe.inventoryapp.backend.location.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;

@Service
public class RegionDomainService {
  private final RegionRepository regionRepository;

  public RegionDomainService(RegionRepository regionRepository) {
    this.regionRepository = regionRepository;
  }

  public void verifyRegionNameAvailable(String name) {
    if (regionRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyRegionNameAvailableExcludingId(String name, Long id) {
    if (regionRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
