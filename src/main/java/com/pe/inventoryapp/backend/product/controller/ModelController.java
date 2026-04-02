package com.pe.inventoryapp.backend.product.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.ModelRequest;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchFirstTenResponse;
import com.pe.inventoryapp.backend.product.service.ModelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/models")
public class ModelController {

  private final ModelService modelService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public ModelController(ModelService modelService, ResponseService responseService,
      ValidationService validationService) {
    this.modelService = modelService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  // Tambien se debe subir una imagen con Cloudinary
  @PostMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CommonResponse> registerModelInProduct(
      @PathVariable Long id,
      @Valid @ModelAttribute ModelRequest modelRequest, 
      BindingResult result, 
      @RequestParam(value = "file", required = false) MultipartFile file
      ) {
    validationService.validateFieldsAndThrowResponse(result);
    modelService.saveModelInProductId(modelRequest, file, id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el modelo de un producto");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping("/product/{id}")
  public ResponseEntity<?> listAllModelsByProductId(
      @PathVariable Long id) {
    List<ModelListResponse> models = modelService.findAllModelsByProductId(id);

    DataResponse<List<ModelListResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        models);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping
  public ResponseEntity<?> listAllModels(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) LocalDate minEntryDate,
      @RequestParam(required = false) LocalDate maxEntryDate,
      @RequestParam(required = false) Boolean status,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long typeId) {
    Pageable pageable = PageRequest.of(page, 20);
    PageResponse<ModelListResponse> models = modelService.searchAllModelsByParams(pageable, keyword, minStock, maxStock,
        minEntryDate, maxEntryDate, status, categoryId, typeId);
    DataResponse<PageResponse<ModelListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, models);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/search")
  public ResponseEntity<?> listActiveModelsByName(
    @RequestParam(defaultValue = "0") Integer page,
    @RequestParam(required = false) String keyword
  ) {
    Pageable pageable = PageRequest.of(page, 10);
    PageResponse<ModelListSearchResponse> models = modelService.searchAllModelsByName(pageable, keyword);
    DataResponse<PageResponse<ModelListSearchResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, models);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);

  }

  @GetMapping("/search/models")
  public ResponseEntity<?> listFirstTenModelsByKeyword(@RequestParam(required = true) String keyword){
    List<ModelListSearchFirstTenResponse> models = modelService.findFirstTenModelsByKeyword(keyword);

    DataResponse<List<ModelListSearchFirstTenResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, models);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getModel(@PathVariable Long id) {
    ModelDetailsResponse model = modelService.findModelById(id);
    DataResponse<ModelDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        model);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CommonResponse> updateModel(
    @PathVariable Long id, 
    @Valid @ModelAttribute ModelRequest modelRequest,
      BindingResult result,
    @RequestParam(value = "file", required = false) MultipartFile file
    ) {
    validationService.validateFieldsAndThrowResponse(result);
    modelService.updateModelById(id, modelRequest,file);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
    "Se actualizo los datos del modelo del producto");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusModel(@PathVariable Long id) {
    modelService.changeStatusModelById(id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado del modelo");
    return ResponseEntity.status(response.status()).body(response);
  }
}

