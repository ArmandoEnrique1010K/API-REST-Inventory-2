package com.pe.inventoryapp.backend.company.model.mapper;

import com.pe.inventoryapp.backend.company.model.entity.Company;
import com.pe.inventoryapp.backend.company.model.response.CompanyResponse;

public class CompanyMapper {
  private Company company;

  private CompanyMapper() {

  }

  public static CompanyMapper builder() {
    return new CompanyMapper();
  }

  public CompanyMapper setCompany(Company company) {
    this.company = company;
    return this;
  }

  public CompanyResponse buildCompanyResponse() {
    if (company == null) {
      throw new RuntimeException("Debe pasar la entidad Company");
    } else {
      return new CompanyResponse(
          company.getId(),
          company.getName().trim());
    }
  }
}
