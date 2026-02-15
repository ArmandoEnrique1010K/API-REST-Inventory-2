package com.pe.inventoryapp.backend.stocklot.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;

@Service
public class CompanyDomainService {
  private final CompanyRepository companyRepository;

  public CompanyDomainService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public void verifyCompanyNameAvailable(String name) {
    if (companyRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  public void verifyCompanyNameAvailableExcludingId(String name, Long id) {
    if (companyRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
