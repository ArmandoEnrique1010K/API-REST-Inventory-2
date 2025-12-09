package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

public class CategoryMapper {
  private Category category;

  private CategoryMapper() {

  }

  public static CategoryMapper builder() {
    return new CategoryMapper();
  }

  public CategoryMapper setCategory(Category category) {
    this.category = category;
    return this;
  }

  public CategoryResponse buildCategoriesResponse() {

    if (category == null) {
      throw new RuntimeException("Debe pasar la entidad Category");
    }
    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new CategoryResponse(
        category.getId(),
        category.getName().trim(),
        category.isStatus());
  }

}
