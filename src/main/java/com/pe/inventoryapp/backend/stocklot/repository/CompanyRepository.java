package com.pe.inventoryapp.backend.stocklot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.stocklot.model.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
