package com.pe.inventoryapp.backend.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.stock.model.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  Optional<Company> findByName(String name);
}
