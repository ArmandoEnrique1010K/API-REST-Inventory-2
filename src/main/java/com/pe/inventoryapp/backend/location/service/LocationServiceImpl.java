package com.pe.inventoryapp.backend.location.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.mapper.LocationMapper;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationDetailsResponse;
import com.pe.inventoryapp.backend.location.model.response.LocationListResponse;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;

@Service
public class LocationServiceImpl implements LocationService {

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Override
  @Transactional
  public String save(LocationRequest locationRequest) {

    Region region = regionRepository.findById(locationRequest.getIdRegion())
        .orElseThrow(() -> new RuntimeException("Region not found"));

    Location location = new Location();
    location.setName(locationRequest.getName());
    location.setStatus(true);
    location.setRegion(region);
    locationRepository.save(location);

    return "Se guardo la ubicación";
  }

  @Override
  @Transactional(readOnly = true)
  public List<LocationListResponse> findAll() {
    List<Location> locations = (List<Location>) locationRepository.findAll();

    return locations.stream().map(location -> LocationMapper.builder().setLocation(
        location).buildLocationListResponse()).collect(Collectors.toList());
  }

  @Override
  public List<LocationListResponse> findAllByStatusTrue() {
    List<Location> locations = (List<Location>) locationRepository.findAllByStatusTrue();

    return locations.stream().map(location -> LocationMapper.builder().setLocation(
        location).buildLocationListResponse()).collect(Collectors.toList());
  }

  @Override
  public List<LocationListResponse> findByRegionId(Long regionId) {
    List<Location> locations = (List<Location>) locationRepository.findByRegionId(regionId);

    return locations.stream()
        .map(location -> LocationMapper.builder().setLocation(location).buildLocationListResponse())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<LocationDetailsResponse> findById(Long id) {
    return locationRepository.findById(id)
        .map(location -> LocationMapper.builder().setLocation(location).buildLocationDetailsResponse());
  }

  @Override
  public String update(Long id, LocationRequest productRequest) {
    Optional<Location> locationById = locationRepository.findById(id);

    if (locationById.isPresent()) {

      Region region = regionRepository.findById(productRequest.getIdRegion()).orElseThrow();

      Location locationData = locationById.orElseThrow();
      locationData.setName(productRequest.getName());
      locationData.setRegion(region);

      locationRepository.save(locationData);
    }

    return "Se actualizo la ubicación";
  }

  @Override
  public void changeStatus(Long id) {
    Location location = locationRepository.findById(id).orElseThrow();
    // Cambia el estado de la categoria a false y lo guarda
    location.setStatus(!location.isStatus());
    locationRepository.save(location);
  }

  @Override
  public void verifyLocationNameExist(String name) {
    if (locationRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La ubicación con ese nombre ya existe, introduzca otra ubicación");
    }
  }

}
