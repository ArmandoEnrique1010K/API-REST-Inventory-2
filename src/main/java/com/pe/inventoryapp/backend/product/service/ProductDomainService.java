package com.pe.inventoryapp.backend.product.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;

@Service
public class ProductDomainService {
  private final ProductRepository productRepository;

  public ProductDomainService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public BigDecimal normalizeDecimal(BigDecimal number) {
    return number == null ? null : number;
  }

  public void verifyProductNameAvailable(String name) {
    if (productRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyProductNameAvailableExcludingId(String name, Long id) {
    if (productRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
