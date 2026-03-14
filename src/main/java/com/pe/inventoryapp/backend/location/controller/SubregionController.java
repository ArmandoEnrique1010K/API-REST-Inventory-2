package com.pe.inventoryapp.backend.location.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.location.model.request.SubregionRequest;
import com.pe.inventoryapp.backend.location.model.response.SubregionResponse;
import com.pe.inventoryapp.backend.location.service.SubregionService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/subregions")
public class SubregionController {
  private final SubregionService subregionService;
  private final ValidationService validationService;
  private final ResponseService responseService;

  public SubregionController(
    SubregionService subregionService, 
    ValidationService validationService,
      ResponseService responseService) {
    this.subregionService = subregionService;
    this.validationService = validationService;
    this.responseService = responseService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse> registerSubregion(@Valid @RequestBody SubregionRequest subRegionRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    subregionService.saveSubregion(subRegionRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro la subregión");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping("/region/{id}")
  public ResponseEntity<?> listAllSubregionsByRegionId(@PathVariable Long id) {
    List<SubregionResponse> subregions = subregionService.findAllSubregionsByRegionId(id); 
    DataResponse<List<SubregionResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, subregions);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getSubregion(@PathVariable Long id) {
    SubregionResponse subregion = subregionService.findSubregionById(id);
    DataResponse<SubregionResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        subregion);
    return ResponseEntity.status(response.status()).body(response);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateSubregion(@PathVariable Long id, @Valid @RequestBody SubregionRequest subregionRequest, BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    subregionService.updateSubregionById(id, subregionRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo el nombre de la subregión");
    return ResponseEntity.status(response.status()).body(response);
  }
}
