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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import jakarta.validation.Valid;

import com.pe.inventoryapp.backend.location.service.RegionService;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;
import com.pe.inventoryapp.backend.location.model.request.RegionRequest;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
  @Autowired
  private RegionService regionService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerRegion(@Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    regionService.saveRegion(regionRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS,
            "Se registro la región"));
  }

  @GetMapping
  public ResponseEntity<?> listAllRegions() {
    List<RegionResponse> regions = regionService.findAllRegions();
    return ResponseEntity.status(200).body(regions);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getRegion(@PathVariable Long id) {
    RegionResponse regionResponse = regionService.findRegionById(id);
    return ResponseEntity.status(200).body(regionResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    regionService.updateRegionById(id, regionRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS,
        "Se actualizo los datos de la región"));
  }
}
