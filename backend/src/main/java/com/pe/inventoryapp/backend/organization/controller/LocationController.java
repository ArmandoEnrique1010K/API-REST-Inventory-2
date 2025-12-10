package com.pe.inventoryapp.backend.organization.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.organization.model.request.LocationRequest;
import com.pe.inventoryapp.backend.organization.model.response.LocationDetailsResponse;
import com.pe.inventoryapp.backend.organization.service.LocationService;

import jakarta.validation.Valid;

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
  public ResponseEntity<CommonResponse> save(@Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    locationService.verifyLocationNameExist(locationRequest.getName());

    var location = locationService.save(locationRequest);

    if (location == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar la ubicación");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", location));
  }

  @GetMapping
  public List<?> listAll() {
    return locationService.findAll();
  }

  @GetMapping("/active")
  public List<?> listAllActive() {
    return locationService.findAllByStatusTrue();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    Optional<LocationDetailsResponse> location = locationService.findById(id);
    if (!location.isPresent()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No se ha encontrado la ubicación"));
    }

    return ResponseEntity.status(200).body(location);
  }

  // Método para listar las ubicaciones por region
  @GetMapping("/region/{id}")
  public List<?> listByRegionId(@PathVariable Long id) {
    return locationService.findByRegionId(id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody LocationRequest locationRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    Optional<LocationDetailsResponse> optionalDetailsResponse = locationService.findById(id);

    String newName = locationRequest.getName();

    if (optionalDetailsResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La ubicación no existe"));
    }

    if (!optionalDetailsResponse.get().getName().equals(newName)) {
      locationService.verifyLocationNameExist(newName);
    }

    if (optionalDetailsResponse.get().isStatus() == false) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "La ubicación se encuentra desactivada"));
    }

    String message = locationService.update(id, locationRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));

  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> disableLocation(@PathVariable Long id) {
    locationService.changeStatus(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(responseService.generateCommonResponse("success", "Se ha cambiado el estado del producto"));
  }

}
