package com.pe.inventoryapp.backend.company.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.company.model.entity.Company;
import com.pe.inventoryapp.backend.company.model.mapper.CompanyMapper;
import com.pe.inventoryapp.backend.company.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.company.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.company.repository.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {

  @Autowired
  private CompanyRepository companyRepository;

  @Override
  @Transactional
  public String save(CompanyRequest companyRequest) {
    Company company = new Company();
    company.setName(companyRequest.getName());
    companyRepository.save(company);
    return "Se guardo la empresa";
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyResponse> findAll() {
    List<Company> companies = (List<Company>) companyRepository.findAll();

    return companies.stream()
        .map(company -> CompanyMapper.builder().setCompany(
            company).buildCompanyResponse())
        .collect(Collectors.toList());

  }

  @Override
  public Optional<CompanyResponse> findById(Long id) {
    return companyRepository.findById(id)
        .map(company -> CompanyMapper.builder().setCompany(company).buildCompanyResponse());
  }

  @Override
  public String update(Long id, CompanyRequest companyRequest) {
    Optional<Company> companyById = companyRepository.findById(id);

    if (companyById.isPresent()) {
      Company companyData = companyById.orElseThrow();

      companyData.setName(companyRequest.getName());

      companyRepository.save(companyData);
    }

    return "Se actualizo la empresa";

  }

  @Override
  public void verifyCompanyNameExist(String name) {
    if (companyRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe, introduzca otra categoria");
    }
  }

}
