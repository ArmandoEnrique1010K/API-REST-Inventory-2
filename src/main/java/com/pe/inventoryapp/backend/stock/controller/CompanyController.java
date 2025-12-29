package com.pe.inventoryapp.backend.stock.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.stock.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.stock.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.stock.service.CompanyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public ResponseEntity<CommonResponse> registerCompany(@Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    companyService.saveCompany(companyRequest);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se registro la empresa en el sistema"));
  }

  @GetMapping
  public ResponseEntity<?> listAllCompanies() {
    List<CompanyResponse> companies = companyService.findAllCompanies();
    return ResponseEntity.status(200).body(companies);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCompany(@PathVariable Long id) {
    CompanyResponse companyResponse = companyService.findCompanyById(id);
    return ResponseEntity.status(200).body(companyResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    companyService.updateCompanyById(id, companyRequest);

    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
        ResponseStatusCodes.SUCCESS_RESPONSE, "Se actualizo los datos de la empresa"));
  }
}
