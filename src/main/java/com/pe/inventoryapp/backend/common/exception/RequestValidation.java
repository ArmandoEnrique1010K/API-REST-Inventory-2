package com.pe.inventoryapp.backend.common.exception;

import java.util.Map;

public class RequestValidation extends RuntimeException {
  private final Map<String, String> errors;

  public RequestValidation(Map<String, String> errors) {
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}