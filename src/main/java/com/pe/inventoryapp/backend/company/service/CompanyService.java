package com.pe.inventoryapp.backend.company.service;

import java.util.List;

import com.pe.inventoryapp.backend.company.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.company.model.response.CompanyResponse;

public interface CompanyService {
  void saveCompany(CompanyRequest companyRequest);

  List<CompanyResponse> findAllCompanies();

  CompanyResponse findCompanyById(Long id);

  void updateCompanyById(Long id, CompanyRequest companyRequest);
}
