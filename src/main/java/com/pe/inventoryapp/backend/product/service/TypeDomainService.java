package com.pe.inventoryapp.backend.product.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;

@Service
public class TypeDomainService {
  private final TypeRepository typeRepository;
  
  public TypeDomainService(TypeRepository typeRepository) {
    this.typeRepository = typeRepository;
  }
  
  public void verifyTypeNameAvailable(String name) {
    if (typeRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyTypeNameAvailableExcludingId(String name, Long id) {
    if (typeRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
