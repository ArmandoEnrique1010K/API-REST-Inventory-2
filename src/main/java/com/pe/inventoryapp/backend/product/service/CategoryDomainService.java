package com.pe.inventoryapp.backend.product.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;

@Service
public class CategoryDomainService {
  private final CategoryRepository categoryRepository;

  public CategoryDomainService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void verifyCategoryNameAvailable(String name) {
    if (categoryRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyCategoryNameAvailableExcludingId(String name, Long id) {
    if (categoryRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
