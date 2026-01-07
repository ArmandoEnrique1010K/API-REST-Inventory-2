package com.pe.inventoryapp.backend.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.product.model.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  @Query("""
      SELECT c 
      FROM Category c 
      WHERE (:status IS NULL OR c.status = :status) ORDER BY c.id ASC
  """)
  List<Category> findAllByParams(
    @Param("status") Boolean status
  );

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
