package com.pe.inventoryapp.backend.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;
import com.pe.inventoryapp.backend.product.service.CategoryService;
import com.pe.inventoryapp.backend.product.service.ProductService;

import jakarta.validation.Valid;

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

  @GetMapping
  public Page<ProductListResponse> listAll(@RequestParam(defaultValue = "0") Integer page) {

    Pageable pageable = PageRequest.of(page, 3);

    return productService.findAll(pageable);
  }

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

}
