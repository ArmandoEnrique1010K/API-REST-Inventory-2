package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;

public interface CategoryService {
  // Guarda una categoria
  public String save(CategoryRequest categoryRequest);

  public List<CategoryResponse> findAll();

  public List<CategoryResponse> findAllByStatusTrue();

  // Cambia el estado de una categoria por su id
  public void changeStatus(Long id);

  // Actualiza una categoria por su id
  public String update(Long id, CategoryRequest categoryRequest);

  // Obtiene una categoria por su id
  public Optional<CategoryResponse> findById(Long id);

  // Obtiene una categoria por su nombre
  public void verifyCategoryNameExist(String name);
}
