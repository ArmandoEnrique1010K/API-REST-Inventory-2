package com.pe.inventoryapp.backend.location.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import jakarta.validation.Valid;

import com.pe.inventoryapp.backend.location.service.RegionService;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;
import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;


@RestController
@RequestMapping("/api/regions")
public class RegionController {
  private final RegionService regionService;
  private final ValidationService validationService;
  private final ResponseService responseService;

  public RegionController(RegionService regionService, ValidationService validationService,
      ResponseService responseService) {
    this.regionService = regionService;
    this.validationService = validationService;
    this.responseService = responseService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<CommonResponse> registerRegion(@Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    regionService.saveRegion(regionRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro la región");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('OPERATOR')")
  @GetMapping
  public ResponseEntity<?> listAllRegions() {
    List<RegionResponse> regions = regionService.findAllRegions();
    DataResponse<List<RegionResponse>> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        regions);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('OPERATOR')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getRegion(@PathVariable Long id) {
    RegionResponse regionResponse = regionService.findRegionById(id);
    DataResponse<RegionResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        regionResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateRegion(@PathVariable Long id,
      @Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    regionService.updateRegionById(id, regionRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo el nombre de la región");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping("/deliveryOrder/{id}")
  public ResponseEntity<?> listAllRegionsByDeliveryOrder(@PathVariable Long id) {
    List<RegionResponse> regions = regionService.findAllRegionsByDeliveryOrder(id);
    DataResponse<List<RegionResponse>> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        regions);
    return ResponseEntity.status(response.status()).body(response);
  }
  
}
