package com.pe.inventoryapp.backend.product.service;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.request.ProductCreateRequest;
import com.pe.inventoryapp.backend.product.model.request.ProductUpdateRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductResponse;

public interface ProductService {
  void saveProduct(ProductCreateRequest productRequest);

  PageResponse<ProductResponse> searchAllProductsByParams(
      Pageable pageable,
      String name,
      Boolean status,
      Long categoryId,
      Long typeId
  );

  ProductResponse findProductById(Long id);

  void updateProductById(Long id, ProductUpdateRequest productUpdateRequest);

  void changeStatusProductById(Long id);
}
