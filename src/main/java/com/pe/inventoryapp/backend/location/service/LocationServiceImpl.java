package com.pe.inventoryapp.backend.location.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.mapper.LocationMapper;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
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
  public void saveLocation(LocationRequest locationRequest) {
    verifyLocationNameExist(locationRequest.getName());

    Long idRegion = locationRequest.getIdRegion();

    if (idRegion == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Region region = regionRepository.findById(
        idRegion)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe"));

    String name = locationRequest.getName().trim();

    Location location = new Location();
    location.setName(name);
    location.setStatus(true);
    location.setRegion(region);

    locationRepository.save(location);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LocationResponse> searchAllLocations(
      String name,
      Long regionId,
      Boolean status,
      Pageable pageable) {
    if (regionId != null && !regionRepository.existsById(regionId)) {
      throw new BusinessException(
          ResponseStatusCodes.ENTITY_NOT_FOUND,
          "La región no existe en el sistema");
    }
    Page<Location> locations = locationRepository.findAllByParams(name, regionId, status, pageable);

    return locations.map(location -> LocationMapper.builder().setLocation(location).buildLocationResponse());
  }

  @Override
  public LocationResponse findLocationById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe en el sistema"));

    if (location.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La ubicación se encuentra desactivada");
    }

    return LocationMapper.builder().setLocation(location).buildLocationResponse();
  }

  @Override
  public void updateLocationById(Long id, LocationRequest locationRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe en el sistema"));

    if (location.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La ubicación se encuentra desactivada");
    }

    String newName = locationRequest.getName().trim();

    verifyLocationNameExistById(newName, id);

    Long idRegion = locationRequest.getIdRegion();

    if (idRegion == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Region region = regionRepository.findById(idRegion)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe en el sistema"));
    
    location.setName(newName);
    location.setRegion(region);

    locationRepository.save(location);
  }

  @Override
  public void changeStatusLocationById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Esta ubicación no se puede inhabilitar");
    }

    Location location = locationRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe en el sistema"));
    location.setStatus(!location.isStatus());
    locationRepository.save(location);
  }

  // METODOS AUXILIARES
  private void verifyLocationNameExist(String name) {
    if (locationRepository.existsByName(name)) {
      throw new FieldValidation("name", "La ubicación con ese nombre ya existe, introduzca otro nombre");
    }
  }

  private void verifyLocationNameExistById(String name, Long id) {
    if (locationRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "La ubicación con ese nombre ya existe, introduzca otro nombre");
    }
  }
}
