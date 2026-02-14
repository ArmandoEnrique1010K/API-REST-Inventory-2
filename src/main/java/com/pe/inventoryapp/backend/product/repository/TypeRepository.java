package com.pe.inventoryapp.backend.product.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.product.model.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
