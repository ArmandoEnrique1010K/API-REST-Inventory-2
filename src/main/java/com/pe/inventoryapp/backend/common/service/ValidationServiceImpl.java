package com.pe.inventoryapp.backend.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.pe.inventoryapp.backend.common.exception.RequestValidation;

@Service
public class ValidationServiceImpl implements ValidationService {

  @Override
  public void validateFieldsAndThrowResponse(BindingResult result) {
    if (result.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      result.getFieldErrors().forEach(err -> {
        errors.put(err.getField(), err.getDefaultMessage());
      });

      // Si se elimina esta linea de codigo, deshabilita la validación de campos
      // Este mensaje queda ignorado, excepto los errores que se pasan
      throw new RequestValidation("", errors);
    }
  }

}