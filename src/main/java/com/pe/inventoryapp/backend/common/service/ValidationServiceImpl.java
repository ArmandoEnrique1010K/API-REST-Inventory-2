package com.pe.inventoryapp.backend.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.pe.inventoryapp.backend.common.exception.RequestValidation;

@Service
public class ValidationServiceImpl implements ValidationService {
  @Transactional(readOnly = true)
  @Override
  public void validateFieldsAndThrowResponse(BindingResult result) {
    if (result.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      result.getFieldErrors().forEach(err -> {
        errors.put(err.getField(), err.getDefaultMessage());
      });

      throw new RequestValidation(errors);
    }
  }
}