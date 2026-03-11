package com.pe.inventoryapp.backend.product.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CommonResponse> registerProduct(
    @Valid @ModelAttribute ProductCreateRequest productCreateRequest,
      BindingResult result,
    @RequestParam(value = "file", required = false) MultipartFile file) {
    validationService.validateFieldsAndThrowResponse(result);
    productService.saveProduct(productCreateRequest, file);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el producto");
    return ResponseEntity.status(response.status()).body(response);
  }

  // Este método lista todos los productos (recordar que cada producto tiene distintos modelos)
  @GetMapping
  public ResponseEntity<?> listAllProducts(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean status,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long typeId) {
            Pageable pageable = PageRequest.of(page, 20);

            PageResponse<ProductResponse> products = productService.searchAllProductsByParams(pageable, name, status, categoryId, typeId);

                DataResponse<PageResponse<ProductResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, products);

    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
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
