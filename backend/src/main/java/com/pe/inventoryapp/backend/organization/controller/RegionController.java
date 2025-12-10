package com.pe.inventoryapp.backend.organization.controller;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
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
import com.pe.inventoryapp.backend.organization.model.request.RegionRequest;
import com.pe.inventoryapp.backend.organization.model.response.RegionResponse;
import com.pe.inventoryapp.backend.organization.service.RegionService;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/region")
public class RegionController {
  @Autowired
  private RegionService regionService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @GetMapping
  public List<?> listAll() {
    return regionService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    Optional<RegionResponse> region = regionService.findById(id);

    if (region.isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No se ha encontrado la región"));
    }

    return ResponseEntity.ok(region.get());

  }

  @PostMapping
  public ResponseEntity<CommonResponse> saveRegion(@Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    regionService.verifyRegionNameExist(regionRequest.getName());

    var region = regionService.save(regionRequest);

    if (region == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar la región");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", region));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RegionRequest regionRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    Optional<RegionResponse> optionalRegionResponse = regionService.findById(id);

    String newName = regionRequest.getName();

    // Si la categoria ya no existe
    if (optionalRegionResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La categoria no existe"));
    }

    // En el caso de que se tome el nombre de una región existente
    // No usar el operador !=, en su lugar utiliza el metodo equals
    if (!optionalRegionResponse.get().getName().equals(newName)) {
      regionService.verifyRegionNameExist(newName);
    }

    if (id == 1) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No se puede actualizar esta región"));
    }

    String message = regionService.update(id, regionRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));
  }
}
