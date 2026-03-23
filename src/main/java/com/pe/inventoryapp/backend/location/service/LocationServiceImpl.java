package com.pe.inventoryapp.backend.location.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.entity.Subregion;
import com.pe.inventoryapp.backend.location.model.mapper.LocationMapper;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.location.repository.RegionRepository;
import com.pe.inventoryapp.backend.location.repository.SubregionRepository;

@Service
public class LocationServiceImpl implements LocationService {
  private final LocationRepository locationRepository;
  private final SubregionRepository subregionRepository;
  private final RegionRepository regionRepository;
  private final LocationDomainService locationDomainService;

  public LocationServiceImpl(LocationRepository locationRepository, SubregionRepository subregionRepository,
      RegionRepository regionRepository, LocationDomainService locationDomainService) {
    this.locationRepository = locationRepository;
    this.subregionRepository = subregionRepository;
    this.regionRepository = regionRepository;
    this.locationDomainService = locationDomainService;
  }

  @Override
  @Transactional
  public void saveLocation(LocationRequest locationRequest) {
    String name = locationRequest.getName().trim();
    Long idSubregion = locationRequest.getSubregionId();

    if (idSubregion == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Subregion subregion = subregionRepository.findById(
        idSubregion)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La subregión no existe"));

    locationDomainService.verifyLocationNameAvailableBySubregionId(name, idSubregion);

    Location location = new Location();
    location.setName(name);
    location.setAddress(locationRequest.getAddress().trim());
    location.setStatus(true);
    location.setSubregion(subregion);

    locationRepository.save(location);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<LocationResponse> searchAllLocations(
      Pageable pageable,
      String name,
      Long regionId,
      Long subregionId,
      Boolean status) {
    if (regionId != null && !regionRepository.existsById(regionId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La región no existe");
    }

    if (subregionId != null && !subregionRepository.existsById(subregionId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La subregión no existe");
    }

    Page<Location> locations = locationRepository.findAllByParams(pageable, name, regionId, subregionId, status);

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

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Esta ubicación no se puede editar");
    }

    Location location = locationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    if (location.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La ubicación se encuentra desactivada");
    }

    String newName = locationRequest.getName().trim();
    String address = locationRequest.getAddress().trim();
    Long idSubregion = locationRequest.getSubregionId();

    if (idSubregion == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    locationDomainService.verifyLocationNameAvailableBySubregionIdExcludingId(newName, idSubregion, id);

    Subregion subregion = subregionRepository.findById(
        idSubregion)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La subregión no existe"));

    location.setName(newName);
    location.setAddress(address);
    location.setSubregion(subregion);
    locationRepository.save(location);
  }

  @Override
  @Transactional
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
}
