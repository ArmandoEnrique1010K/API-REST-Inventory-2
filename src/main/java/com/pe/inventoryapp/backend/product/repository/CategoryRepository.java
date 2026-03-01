package com.pe.inventoryapp.backend.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.pe.inventoryapp.backend.product.model.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Query("SELECT c FROM Category c ORDER BY c.id DESC")
  List<Category> findAllAndSortById();

  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
