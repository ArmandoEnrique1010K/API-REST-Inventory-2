package com.pe.inventoryapp.backend.company.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
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
  public void saveCompany(CompanyRequest companyRequest) {
    verifyCompanyNameExist(companyRequest.getName());

    Company company = new Company();
    company.setName(companyRequest.getName());

    companyRepository.save(company);
  }
  // return "Se guardo la empresa";

  @Override
  @Transactional(readOnly = true)
  public List<CompanyResponse> findAllCompanies() {
    List<Company> companies = (List<Company>) companyRepository.findAll();

    return companies.stream()
        .map(company -> CompanyMapper.builder().setCompany(
            company).buildCompanyResponse())
        .collect(Collectors.toList());
  }

  @Override
  public CompanyResponse findCompanyById(Long id) {

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La empresa no existe"));

    return CompanyMapper.builder().setCompany(company).buildCompanyResponse();
  }

  @Override
  public void updateCompanyById(Long id, CompanyRequest companyRequest) {

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Esta empresa no se puede editar");
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La empresa no existe"));

    verifyCompanyNameExist(companyRequest.getName().trim());

    company.setName(companyRequest.getName().trim());

    companyRepository.save(company);
  }
  // return "Se actualizo la empresa";

  // METODOS PRIVADOS
  private void verifyCompanyNameExist(String name) {
    if (companyRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La empresa con ese nombre ya existe, introduzca otra empresa");
    }
  }

}
