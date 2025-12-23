package com.pe.inventoryapp.backend.location.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;

public interface LocationService {

  void saveLocation(LocationRequest locationRequest);

  Page<LocationResponse> searchAllLocations(
      String name,
      Long regionId,
      Boolean status,
      Pageable pageable);

  LocationResponse findLocationById(Long id);

  void updateLocationById(Long id, LocationRequest productRequest);

  void changeStatusLocationById(Long id);
}
