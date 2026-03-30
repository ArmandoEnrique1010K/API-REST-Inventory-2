package com.pe.inventoryapp.backend.product.service;

import java.util.List;

import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

public interface CategoryService {
  void saveCategory(CategoryRequest categoryRequest);

  List<CategoryResponse> findAllCategories();

  List<CategoryResponse> findAllActiveCategories();

  CategoryResponse findCategoryById(Long id);

  void updateCategoryById(Long id, CategoryRequest categoryRequest);

  void changeStatusCategoryById(Long id);
}
