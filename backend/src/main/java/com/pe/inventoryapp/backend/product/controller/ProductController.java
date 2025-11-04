package com.pe.inventoryapp.backend.product.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;
import com.pe.inventoryapp.backend.product.service.CategoryService;
import com.pe.inventoryapp.backend.product.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/product")
public class ProductController {

  @Autowired
  private ProductService productService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @PostMapping
  public ResponseEntity<CommonResponse> save(@Valid @RequestBody ProductRequest productRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    productService.verifyProductNameExist(productRequest.getName());

    var product = productService.save(productRequest);

    if (product == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el producto");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", product));
  }

  @GetMapping
  public Page<ProductListResponse> listAll(@RequestParam(defaultValue = "0") Integer page) {

    Pageable pageable = PageRequest.of(page, 10);

    return productService.findAll(pageable);
  }

  @GetMapping("/active")
  public Page<ProductListResponse> findAllActive(@RequestParam(defaultValue = "0") Integer page) {
    Pageable pageable = PageRequest.of(page, 10);

    return productService.findAllByStatusTrue(pageable);
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> disableProduct(@PathVariable Long id) {
    productService.changeStatus(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(responseService.generateCommonResponse("success", "Se ha cambiado el estado del producto"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> putMethodName(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    Optional<ProductDetailsResponse> optionalProductResponse = productService.findById(id);

    // Campo cuyo nombre no se debe repetir
    String newName = productRequest.getName();
    if (!optionalProductResponse.get().getName().equals(newName)) {
      productService.verifyProductNameExist(newName);
    }

    if (optionalProductResponse.get().isStatus() == false) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "La categoria se encuentra desactivada"));
    }

    // Si el producto ya no existe
    if (optionalProductResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "El producto no existe"));
    }

    String message = productService.update(id, productRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));
  }
}
