package com.pe.inventoryapp.backend.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.company.model.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  Optional<Company> findByName(String name);
}
