package com.pe.inventoryapp.backend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.product.model.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  List<Category> findAllByStatusTrue();

  Optional<Category> findByName(String name);

  Boolean existsByName(String name);
}
