package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
import com.pe.inventoryapp.backend.location.model.response.SearchLocationResponse;

public interface LocationService {
  void saveLocation(LocationRequest locationRequest);

  PageResponse<LocationResponse> searchAllLocations(
      Pageable pageable,
      String name,
      Long regionId,
      Long subregionId,
      Boolean status);

  LocationResponse findLocationById(Long id);

  void updateLocationById(Long id, LocationRequest productRequest);

  void changeStatusLocationById(Long id);

  List<SearchLocationResponse> findFirstTenLocationsByNameAndRegionIdAndSubregionId(String name, Long regionId, Long subregionId);
}
