package com.pe.inventoryapp.backend.location.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.validation.Valid;

import com.pe.inventoryapp.backend.location.service.LocationService;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
import com.pe.inventoryapp.backend.location.model.response.SearchLocationResponse;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
  private final LocationService locationService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public LocationController(
      LocationService locationService,
      ValidationService validationService,
      ResponseService responseService) {
    this.locationService = locationService;
    this.validationService = validationService;
    this.responseService = responseService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<CommonResponse> registerLocation(@Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    locationService.saveLocation(locationRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro la ubicación");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllLocations(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long regionId,
      @RequestParam(required = false) Long subregionId,
      @RequestParam(required = false) Boolean status,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction

  ) {
    Sort sort = buildSort(sortBy, direction);
    Pageable pageable = PageRequest.of(page, 20, sort);

    PageResponse<LocationResponse> locations = locationService.searchAllLocations(pageable, name,
        regionId, subregionId, status);

    DataResponse<PageResponse<LocationResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        locations);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/search/region/{regionId}/subregion/{subregionId}")
  public ResponseEntity<?> listFirstTenLocationsByKeyword(@RequestParam(required = true) String name,
      @PathVariable Long regionId, @PathVariable Long subregionId) {
    List<SearchLocationResponse> locations = locationService.findFirstTenLocationsByNameAndRegionIdAndSubregionId(name,
        regionId,
        subregionId);
    DataResponse<List<SearchLocationResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        locations);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getLocation(@PathVariable Long id) {
    LocationResponse locationResponse = locationService.findLocationById(id);
    DataResponse<LocationResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        locationResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    locationService.updateLocationById(id, locationRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo los datos de la ubicación");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusLocation(@PathVariable Long id) {
    locationService.changeStatusLocationById(id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado de la ubiación");
    return ResponseEntity.status(response.status()).body(response);
  }

  private Sort buildSort(String sortBy, String direction) {

    String field = switch (sortBy) {
      case "id" -> "id";
      case "name" -> "name";
      case "subregionName" -> "subregion.name";
      default -> "id";
    };

    Sort.Direction dir = direction.equalsIgnoreCase("asc")
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;

    return Sort.by(dir, field);
  }
}
