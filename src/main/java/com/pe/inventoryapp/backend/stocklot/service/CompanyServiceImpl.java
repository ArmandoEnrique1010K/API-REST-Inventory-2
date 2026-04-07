package com.pe.inventoryapp.backend.stocklot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.mapper.CompanyMapper;
import com.pe.inventoryapp.backend.stocklot.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {
  private final CompanyRepository companyRepository;
  private final CompanyDomainService companyDomainService;

  public CompanyServiceImpl(CompanyRepository companyRepository, CompanyDomainService companyDomainService) {
    this.companyRepository = companyRepository;
    this.companyDomainService = companyDomainService;
  }

  @Override
  @Transactional
  public void saveCompany(CompanyRequest companyRequest) {
    String name = companyRequest.getName().trim();

    companyDomainService.verifyCompanyNameAvailable(name);

    Company company = new Company();
    company.setName(name);

    companyRepository.save(company);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyResponse> findAllCompanies() {
    List<Company> companies = (List<Company>) companyRepository.findAllAndSortById();

    return companies.stream()
        .map(company -> CompanyMapper.builder().setCompany(
            company).buildCompanyResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public CompanyResponse findCompanyById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La empresa no existe"));

    return CompanyMapper.builder().setCompany(company).buildCompanyResponse();
  }

  @Override
  @Transactional
  public void updateCompanyById(Long id, CompanyRequest companyRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "No se puede cambiar el nombre de esta empresa");
    }

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La empresa no existe"));

    String newName = companyRequest.getName().trim();

    companyDomainService.verifyCompanyNameAvailableExcludingId(newName, id);
    company.setName(newName);

    companyRepository.save(company);
  }
}
