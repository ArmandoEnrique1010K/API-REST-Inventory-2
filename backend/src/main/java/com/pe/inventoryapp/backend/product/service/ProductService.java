package com.pe.inventoryapp.backend.product.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;

public interface ProductService {

  String save(ProductRequest productRequest);

  Page<ProductListResponse> findAll(Pageable pageable);

  Page<ProductListResponse> findAllByStatusTrue(Pageable pageable);

  public void verifyProductNameExist(String name);

  public void changeStatus(Long id);

  public Optional<ProductDetailsResponse> findById(Long id);

  public String update(Long id, ProductRequest productRequest);

}
