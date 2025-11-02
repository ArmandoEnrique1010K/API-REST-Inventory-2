package com.pe.inventoryapp.backend.product.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.dto.CategoryDto;
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
  public List<?> listAll() {
    return categoryService.findAll();
  }

  @GetMapping("/active")
  public List<?> listAllActive() {
    return categoryService.findAllByStatusTrue();
  }

  @PostMapping
  public ResponseEntity<CommonResponse> saveCategory(@Valid @RequestBody CategoryDto categoryResponse,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    categoryService.verifyCategoryNameExist(categoryResponse.getName());

    var category = categoryService.save(categoryResponse);

    if (category == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el usuario");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", category));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryResponse,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    Optional<CategoryDto> optionalCategoryResponse = categoryService.findById(id);

    String currentName = categoryService.findById(id).get().getName();
    String newName = categoryResponse.getName();

    // No usar el operador !=, en su lugar utiliza el metodo equals
    if (!currentName.equals(newName)) {
      categoryService.verifyCategoryNameExist(categoryResponse.getName());
    }

    if (optionalCategoryResponse.get().getStatus() == false) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "La categoria se encuentra desactivada"));
    }

    // Si la categoria ya no existe
    if (optionalCategoryResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La categoria no existe"));
    }

    String message = categoryService.update(id, categoryResponse);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> disableCategory(@PathVariable Long id) {
    categoryService.changeStatus(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(responseService.generateCommonResponse("success", "Se ha cambiado el estado de la categoria"));
  }
}
