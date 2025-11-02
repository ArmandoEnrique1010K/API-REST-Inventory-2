package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

public interface CategoryService {
  // Guarda una categoria
  public String save(CategoryResponse category);

  public List<CategoryResponse> findAll();

  public List<CategoryResponse> findAllByStatusTrue();

  // Obtiene una categoria por su id
  public Optional<CategoryResponse> findById(Long id);

  // Obtiene una categoria por su nombre
  public Optional<CategoryResponse> findByName(String name);

  // Obtiene el estado de una categoria por su id
  public Boolean getStatusById(Long id);

  // Actualiza una categoria por su id
  public Optional<CategoryResponse> update(Long id, CategoryResponse categoryRequest);

  public void verifyUserEmailExists(String email);
}
