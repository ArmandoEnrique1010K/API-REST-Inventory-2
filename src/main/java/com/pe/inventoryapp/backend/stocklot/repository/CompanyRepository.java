package com.pe.inventoryapp.backend.stocklot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.stocklot.model.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {  
  
  @Query("SELECT c FROM Company c ORDER BY c.id DESC")
  List<Company> findAllAndSortById();

  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);
}
