package com.pe.inventoryapp.backend.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;
import com.pe.inventoryapp.backend.product.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/product")
public class ProductController {

  @Autowired
  private ProductService productService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerProduct(@Valid @RequestBody ProductRequest productRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);

    productService.saveProduct(productRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se guardo el producto"));
  }

  @GetMapping
  public ResponseEntity<?> listAllProducts(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Boolean status) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, categoryId,
        status,
        pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllActiveProducts(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Long categoryId) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock,
        categoryId, true, pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/category/{id}")
  public ResponseEntity<?> listAllProductsByCategory(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Boolean status) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, id,
        status,
        pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/active/category/{id}")
  public ResponseEntity<?> listAllActiveProductsByCategory(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock,
        id, true, pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProduct(@PathVariable Long id) {
    ProductDetailsResponse productDetailsResponse = productService.findProductById(id);
    return ResponseEntity.status(200).body(productDetailsResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    productService.updateProductById(id, productRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE,
        "Se actualizo el producto"));
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<CommonResponse> changeStatusProduct(@PathVariable Long id) {
    productService.changeStatusProductById(id);
    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha cambiado el estado del producto"));
  }
}
