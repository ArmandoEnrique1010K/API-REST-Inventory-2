package com.pe.inventoryapp.backend.company.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.company.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.company.model.response.CompanyResponse;

public interface CompanyService {
  String save(CompanyRequest companyRequest);

  List<CompanyResponse> findAll();

  Optional<CompanyResponse> findById(Long id);

  String update(Long id, CompanyRequest companyRequest);

  void verifyCompanyNameExist(String name);
}
