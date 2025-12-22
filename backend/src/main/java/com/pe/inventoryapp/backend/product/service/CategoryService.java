package com.pe.inventoryapp.backend.product.service;

import java.util.List;

import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

public interface CategoryService {
  // Guarda una categoria
  void saveCategory(CategoryRequest categoryRequest);

  List<CategoryResponse> findAllCategories();

  List<CategoryResponse> findAllCategoriesByStatusTrue();

  // Obtiene una categoria por su id
  CategoryResponse findCategoryById(Long id);

  // Actualiza una categoria por su id
  void updateCategoryById(Long id, CategoryRequest categoryRequest);

  // Cambia el estado de una categoria por su id
  void changeStatusCategoryById(Long id);
}
