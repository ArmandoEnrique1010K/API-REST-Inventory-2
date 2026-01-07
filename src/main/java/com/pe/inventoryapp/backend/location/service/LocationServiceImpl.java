package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
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
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Region region = regionRepository.findById(
        idRegion)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La región no existe"));

    String name = locationRequest.getName().trim();

    Location location = new Location();
    location.setName(name);
    location.setStatus(true);
    location.setRegion(region);

    locationRepository.save(location);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<LocationResponse> searchAllLocations(
      String name,
      Long regionId,
      Boolean status,
      Pageable pageable) {
    if (regionId != null && !regionRepository.existsById(regionId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La región no existe");
    }
    Page<Location> locations = locationRepository.findAllByParams(name, regionId, status, pageable);

    List<LocationResponse> result = locations.getContent().stream().map(location -> LocationMapper.builder().setLocation(location).buildLocationResponse()).toList();

    PageResponse<LocationResponse> pageResponse = new PageResponse<>(
        result,
        locations.getNumber(),
        locations.getSize(),
        locations.getTotalElements(),
        locations.getTotalPages(),
        locations.isFirst(),
        locations.isLast()
    );

    return pageResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public LocationResponse findLocationById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    if (location.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La ubicación se encuentra desactivada");
    }

    return LocationMapper.builder().setLocation(location).buildLocationResponse();
  }

  @Override
  @Transactional
  public void updateLocationById(Long id, LocationRequest locationRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    if (location.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La ubicación se encuentra desactivada");
    }

    String newName = locationRequest.getName().trim();

    verifyLocationNameExistById(newName, id);

    Long idRegion = locationRequest.getIdRegion();

    if (idRegion == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Region region = regionRepository.findById(idRegion)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La región no existe"));
    
    location.setName(newName);
    location.setRegion(region);

    locationRepository.save(location);
  }

  @Override
  public void changeStatusLocationById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Esta ubicación no se puede inhabilitar");
    }

    Location location = locationRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));
    location.setStatus(!location.isStatus());
    locationRepository.save(location);
  }

  // METODOS AUXILIARES
  private void verifyLocationNameExist(String name) {
    if (locationRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  private void verifyLocationNameExistById(String name, Long id) {
    if (locationRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
