package com.pe.inventoryapp.backend.product.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
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
@RequestMapping("/api/category")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @GetMapping
  public ResponseEntity<?> listAll() {
    List<CategoryResponse> categories = categoryService.findAll();
    return ResponseEntity.status(200).body(categories);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllActive() {
    List<CategoryResponse> categories = categoryService.findAllByStatusTrue();
    return ResponseEntity.status(200).body(categories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    CategoryResponse categoryResponse = categoryService.findById(id);
    return ResponseEntity.status(200).body(categoryResponse);
  }

  @PostMapping
  public ResponseEntity<CommonResponse> saveCategory(@Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.save(categoryRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se guardo la categoria"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.update(id, categoryRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se actualizo la categoria"));
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> disableCategory(@PathVariable Long id) {
    categoryService.changeStatus(id);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se ha cambiado el estado de la categoria"));
  }
}