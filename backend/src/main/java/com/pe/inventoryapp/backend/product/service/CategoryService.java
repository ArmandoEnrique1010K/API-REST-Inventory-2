package com.pe.inventoryapp.backend.product.service;

import java.util.List;

import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

public interface CategoryService {
  // Guarda una categoria
  void save(CategoryRequest categoryRequest);

  List<CategoryResponse> findAll();

  List<CategoryResponse> findAllByStatusTrue();

  // Obtiene una categoria por su id
  CategoryResponse findById(Long id);

  // Actualiza una categoria por su id
  void update(Long id, CategoryRequest categoryRequest);

  // Cambia el estado de una categoria por su id
  void changeStatus(Long id);
}
