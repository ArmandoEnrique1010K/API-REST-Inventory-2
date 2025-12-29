package com.pe.inventoryapp.backend.product.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.product.model.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  List<Category> findAllByStatusTrue();

  // Optional<Category> findByName(String name);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
