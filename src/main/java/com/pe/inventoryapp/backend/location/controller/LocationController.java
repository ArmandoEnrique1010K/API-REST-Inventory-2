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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;

import com.pe.inventoryapp.backend.location.service.LocationService;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
import com.pe.inventoryapp.backend.location.model.request.LocationRequest;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;

@RestController
@RequestMapping("/api/location")
public class LocationController {
  @Autowired
  private LocationService locationService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerLocation(@Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    locationService.saveLocation(locationRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se guardo la ubicación"));
  }

  @GetMapping
  public ResponseEntity<?> listAllLocations(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long regionId,
      @RequestParam(required = false) Boolean status) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<LocationResponse> locations = locationService.searchAllLocations(name, regionId, status, pageable);

    return ResponseEntity.status(200).body(locations);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllActiveLocations(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long regionId) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<LocationResponse> locations = locationService.searchAllLocations(name, regionId, true, pageable);

    return ResponseEntity.status(200).body(locations);
  }

  @GetMapping("/region/{id}")
  public ResponseEntity<?> listAllLocationsByRegion(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean status) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<LocationResponse> locations = locationService.searchAllLocations(name, id, status, pageable);

    return ResponseEntity.status(200).body(locations);
  }

  @GetMapping("/active/region/{id}")
  public ResponseEntity<?> listAllActiveLocationsByRegion(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<LocationResponse> locations = locationService.searchAllLocations(name, id, true, pageable);

    return ResponseEntity.status(200).body(locations);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getLocation(@PathVariable Long id) {
    LocationResponse locationResponse = locationService.findLocationById(id);
    return ResponseEntity.status(200).body(locationResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    locationService.updateLocationById(id, locationRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se actualizo la ubicación"));
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> disableLocation(@PathVariable Long id) {
    locationService.changeStatusLocationById(id);
    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha cambiado el estado de la ubicación"));
  }
}
