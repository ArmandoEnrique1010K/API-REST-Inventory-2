package com.pe.inventoryapp.backend.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;

public interface ProductService {
  void save(ProductRequest productRequest);

  Page<ProductListResponse> searchAllByParams(
      String name,
      Integer minStock,
      Integer maxStock,
      Long categoryId,
      Boolean status,
      Pageable pageable);

  Page<ProductListResponse> searchAllByParamsAndStatusTrue(
      String name,
      Integer minStock,
      Integer maxStock,
      Long categoryId,
      Pageable pageable);

  ProductDetailsResponse findById(Long id);

  void update(Long id, ProductRequest productRequest);

  void changeStatus(Long id);
}
