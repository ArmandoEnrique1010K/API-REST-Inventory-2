package com.pe.inventoryapp.backend.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.product.model.request.ProductCreateRequest;
import com.pe.inventoryapp.backend.product.model.request.ProductUpdateRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductResponse;
import com.pe.inventoryapp.backend.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public ProductController(
      ProductService productService,
      ResponseService responseService,
      ValidationService validationService) {
    this.productService = productService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse> registerProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    productService.saveProduct(productCreateRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el producto");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProduct(@PathVariable Long id) {
    ProductResponse productResponse = productService.findProductById(id);
    DataResponse<ProductResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        productResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommonResponse> updateProduct(@PathVariable Long id,
      @Valid @RequestBody ProductUpdateRequest productUpdateRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    productService.updateProductById(id, productUpdateRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizado los datos del producto");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusProduct(@PathVariable Long id) {
    productService.changeStatusProductById(id);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado del producto");

    return ResponseEntity.status(response.status()).body(response);
  }
}
