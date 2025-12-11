package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationDetailsResponse;
import com.pe.inventoryapp.backend.location.model.response.LocationListResponse;

public interface LocationService {

  String save(LocationRequest locationRequest);

  List<LocationListResponse> findAll();

  List<LocationListResponse> findAllByStatusTrue();

  List<LocationListResponse> findByRegionId(Long regionId);

  public Optional<LocationDetailsResponse> findById(Long id);

  public String update(Long id, LocationRequest productRequest);

  public void changeStatus(Long id);

  public void verifyLocationNameExist(String name);

}
