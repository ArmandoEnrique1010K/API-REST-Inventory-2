package com.pe.inventoryapp.backend.stocklot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.mapper.CompanyMapper;
import com.pe.inventoryapp.backend.stocklot.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {

  @Autowired
  private CompanyRepository companyRepository;

  @Override
  @Transactional
  public void saveCompany(CompanyRequest companyRequest) {
    String name = companyRequest.getName().trim();

    verifyCompanyNameExist(name);

    Company company = new Company();
    company.setName(name);

    companyRepository.save(company);
  }

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
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La empresa no existe en el sistema"));

    return CompanyMapper.builder().setCompany(company).buildCompanyResponse();
  }

  @Override
  public void updateCompanyById(Long id, CompanyRequest companyRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "Esta empresa no se puede editar");
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La empresa no existe en el sistema"));

    String newName = companyRequest.getName().trim();

    verifyCompanyNameExistById(newName, id);

    company.setName(newName);

    companyRepository.save(company);
  }
  // return "Se actualizo la empresa";

  // METODOS AUXILIARES
  private void verifyCompanyNameExist(String name) {
    if (companyRepository.existsByName(name)) {
      throw new FieldValidation("name", "La empresa con ese nombre ya existe, introduzca otro nombre");
    }
  }

  private void verifyCompanyNameExistById(String name, Long id) {
    if (companyRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "La empresa con ese nombre ya existe, introduzca otro nombre");
    }
  }
}
