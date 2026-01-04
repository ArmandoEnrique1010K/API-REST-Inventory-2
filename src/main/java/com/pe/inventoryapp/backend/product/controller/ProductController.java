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
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
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
@RequestMapping("/api/products")
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
        .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS_RESPONSE,
            "Se registro el producto en el sistema"));
  }

  @GetMapping
  public ResponseEntity<?> listAllProducts(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Boolean status,
      @RequestParam(required = false) Long categoryId
    ) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, 
        status, categoryId,
        pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/active")
  public ResponseEntity<?> listAllActiveProducts(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Long categoryId
    ) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, true, categoryId, pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/category/{idCategory}")
  public ResponseEntity<?> listAllProductsByCategory(
      @PathVariable Long idCategory,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock,
      @RequestParam(required = false) Boolean status) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, 
        status, idCategory,
        pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/active/category/{idCategory}")
  public ResponseEntity<?> listAllActiveProductsByCategory(
      @PathVariable Long idCategory,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minStock,
      @RequestParam(required = false) Integer maxStock) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<ProductListResponse> products = productService.searchAllProductsByParams(name, minStock, maxStock, true,
        idCategory, pageable);

    return ResponseEntity.status(200).body(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProduct(@PathVariable Long id) {
    ProductDetailsResponse productDetailsResponse = productService.findProductById(id);
    return ResponseEntity.status(200).body(productDetailsResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    productService.updateProductById(id, productRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatus.SUCCESS_RESPONSE,
        "Se actualizo los datos del producto"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusProduct(@PathVariable Long id) {
    productService.changeStatusProductById(id);
    
    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS_RESPONSE,
            "Se ha cambiado el estado del producto"));
  }
}
