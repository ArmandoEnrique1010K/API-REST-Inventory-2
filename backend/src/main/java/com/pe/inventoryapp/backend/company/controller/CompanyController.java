package com.pe.inventoryapp.backend.company.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.company.model.request.CompanyRequest;
import com.pe.inventoryapp.backend.company.model.response.CompanyResponse;
import com.pe.inventoryapp.backend.company.service.CompanyService;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @GetMapping
  public List<?> listAll() {
    return companyService.findAll();
  }

  @GetMapping("/{id}")
  public Optional<CompanyResponse> findById(@PathVariable Long id) {
    return companyService.findById(id);
  }

  @PostMapping
  public ResponseEntity<CommonResponse> saveCategory(@Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    companyService.verifyCompanyNameExist(companyRequest.getName());

    var company = companyService.save(companyRequest);

    if (company == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar la empresa");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", company));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CompanyRequest companyRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);

    Optional<CompanyResponse> optionalCompanyResponse = companyService.findById(id);

    String newName = companyRequest.getName();

    // Si la categoria ya no existe
    if (optionalCompanyResponse.isEmpty()) {
      return ResponseEntity.status(400).body(responseService.generateCommonResponse("error", "La empresa no existe"));
    }

    // No usar el operador !=, en su lugar utiliza el metodo equals
    if (!optionalCompanyResponse.get().getName().equals(newName)) {
      companyService.verifyCompanyNameExist(newName);
    }

    if (id == 1) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No se puede actualizar esta empresa"));
    }

    String message = companyService.update(id, companyRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));
  }

}
