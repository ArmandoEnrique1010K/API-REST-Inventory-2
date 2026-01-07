package com.pe.inventoryapp.backend.product.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;
import com.pe.inventoryapp.backend.product.service.CategoryService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerCategory(@Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.saveCategory(categoryRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se ha registrado la categoria en el sistema");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllCategories(
    @RequestParam(required = false) Boolean status
  ) {
    List<CategoryResponse> categories = categoryService.searchAllCategoriesByStatus(status);
    DataResponse<List<CategoryResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        categories);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllCategoriesActive() {
    List<CategoryResponse> categories = categoryService.searchAllCategoriesByStatus(true);
    DataResponse<List<CategoryResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        categories);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCategory(@PathVariable Long id) {
    CategoryResponse category = categoryService.findCategoryById(id);
    DataResponse<CategoryResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        category);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.updateCategoryById(id, categoryRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
    "Se actualizo el nombre de la categoria");
    return ResponseEntity.status(response.status()).body(response);

  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusCategory(@PathVariable Long id) {
    categoryService.changeStatusCategoryById(id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado de la categoria");
    return ResponseEntity.status(response.status()).body(response);
  }
}