package com.pe.inventoryapp.backend.common.service;

import org.springframework.validation.BindingResult;

public interface ValidationService {
  public void validateFieldsAndThrowResponse(BindingResult result);
}
