package com.pe.inventoryapp.backend.product.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;
  private final ValidationService validationService;
  private final ResponseService responseService;

  public CategoryController(
      CategoryService categoryService, ResponseService responseService,
      ValidationService validationService) {
    this.categoryService = categoryService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse> registerCategory(@Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.saveCategory(categoryRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro la categoria");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllCategories() {
    List<CategoryResponse> categories = categoryService.findAllCategories();
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
  public ResponseEntity<CommonResponse> updateCategory(@PathVariable Long id,
      @Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.updateCategoryById(id, categoryRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo el nombre de la categoria");
    return ResponseEntity.status(response.status()).body(response);
  }
}