package com.pe.inventoryapp.backend.product.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.product.model.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {

  @Query("SELECT t FROM Type t ORDER BY t.id DESC")
  List<Type> findAllAndSortById();

  @Query("SELECT t FROM Type t WHERE t.status = true ORDER BY t.id DESC")
  List<Type> findAllActivesAndSortById();

  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
