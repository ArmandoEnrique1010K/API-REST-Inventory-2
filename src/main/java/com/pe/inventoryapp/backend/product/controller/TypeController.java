package com.pe.inventoryapp.backend.product.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.TypeRequest;
import com.pe.inventoryapp.backend.product.model.response.TypeResponse;
import com.pe.inventoryapp.backend.product.service.TypeService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/types")
public class TypeController {

  private final TypeService typeService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public TypeController(
      TypeService typeService,
      ResponseService responseService,
      ValidationService validationService) {
    this.typeService = typeService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<CommonResponse> registerType(@Valid @RequestBody TypeRequest typeRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    typeService.saveType(typeRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el tipo");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllTypes() {
    List<TypeResponse> types = typeService.listAllTypes();
    DataResponse<List<TypeResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, types);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllActiveTypes() {
    List<TypeResponse> types = typeService.findAllActiveTypes();
    DataResponse<List<TypeResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        types);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getType(@PathVariable Long id) {
    TypeResponse type = typeService.findTypeById(id);
    DataResponse<TypeResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, type);

    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateType(@PathVariable Long id, @Valid @RequestBody TypeRequest typeRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    typeService.updateTypeById(id, typeRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo el nombre del tipo");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusType(@PathVariable Long id) {
    typeService.changeStatusTypeById(id);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado del tipo");

    return ResponseEntity.status(response.status()).body(response);
  }

}
