package com.pe.inventoryapp.backend.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.stock.model.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
