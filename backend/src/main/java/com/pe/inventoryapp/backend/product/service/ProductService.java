package com.pe.inventoryapp.backend.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;

public interface ProductService {

  String save(ProductRequest productRequest);

  Page<ProductListResponse> findAll(Pageable pageable);

  public void verifyProductNameExist(String name);

}
