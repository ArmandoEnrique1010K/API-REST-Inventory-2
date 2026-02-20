package com.pe.inventoryapp.backend.stocklot.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.stocklot.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.stocklot.service.CompanyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
  private final CompanyService companyService;
  private final ValidationService validationService;
  private final ResponseService responseService;

  public CompanyController(
      CompanyService companyService, ResponseService responseService,
      ValidationService validationService) {
    this.companyService = companyService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse> registerCompany(@Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    companyService.saveCompany(companyRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro la empresa");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllCompanies() {
    List<CompanyResponse> companies = companyService.findAllCompanies();
    DataResponse<List<CompanyResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        companies);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCompany(@PathVariable Long id) {
    CompanyResponse companyResponse = companyService.findCompanyById(id);
    DataResponse<CompanyResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        companyResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    companyService.updateCompanyById(id, companyRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se actualizo el nombre de la empresa");
    return ResponseEntity.status(response.status()).body(response);
  }
}
