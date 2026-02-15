package com.pe.inventoryapp.backend.location.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;

@Service
public class LocationDomainService {
  private final LocationRepository locationRepository;

  public LocationDomainService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

    public void verifyLocationNameAvailableBySubregionId(String name, Long subregionId) {
    if (locationRepository.existsByNameAndSubregionId(name, subregionId)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyLocationNameAvailableBySubregionIdExcludingId(String name, Long subregionId, Long id) {
    if (locationRepository.existsByNameAndSubregionIdAndIdNot(name, subregionId, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }

}
