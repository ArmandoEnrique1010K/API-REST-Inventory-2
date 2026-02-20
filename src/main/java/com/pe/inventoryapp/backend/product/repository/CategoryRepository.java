package com.pe.inventoryapp.backend.product.repository;

import org.springframework.data.repository.CrudRepository;
import com.pe.inventoryapp.backend.product.model.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
