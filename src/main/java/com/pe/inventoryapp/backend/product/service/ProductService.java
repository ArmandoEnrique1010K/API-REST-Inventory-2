package com.pe.inventoryapp.backend.product.service;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;

public interface ProductService {
  void saveProduct(ProductRequest productRequest);

  PageResponse<ProductListResponse> searchAllProductsByParams(
      String name,
      Integer minStock,
      Integer maxStock,
      Boolean status,
      Long categoryId,
      Pageable pageable);

  ProductDetailsResponse findProductById(Long id);

  void updateProductById(Long id, ProductRequest productRequest);

  void changeStatusProductById(Long id);
}
