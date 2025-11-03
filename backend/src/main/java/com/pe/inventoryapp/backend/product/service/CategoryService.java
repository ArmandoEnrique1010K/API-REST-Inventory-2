package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.CategoryListResponse;

public interface CategoryService {
  // Guarda una categoria
  public String save(CategoryRequest categoryRequest);

  public List<CategoryListResponse> findAll();

  public List<CategoryListResponse> findAllByStatusTrue();

  // Cambia el estado de una categoria por su id
  public void changeStatus(Long id);

  // Actualiza una categoria por su id
  public String update(Long id, CategoryRequest categoryRequest);

  // Obtiene una categoria por su id
  public Optional<CategoryDetailsResponse> findById(Long id);

  // Obtiene una categoria por su nombre
  public Optional<CategoryDetailsResponse> findByName(String name);

  // Obtiene el estado de una categoria por su id
  public Boolean getStatusById(Long id);

  public void verifyCategoryNameExist(String name);
}
