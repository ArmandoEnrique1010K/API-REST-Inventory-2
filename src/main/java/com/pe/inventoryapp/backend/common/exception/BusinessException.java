package com.pe.inventoryapp.backend.common.exception;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;

public class BusinessException extends RuntimeException {
  private final ResponseStatus ResponseStatusCodes;

  // Constructor para un mensaje predeterminado
  public BusinessException(ResponseStatus ResponseStatusCodes) {
    super(ResponseStatusCodes.getDefaultMessage());
    this.ResponseStatusCodes = ResponseStatusCodes;
  }

  // Constructor para un mensaje personalizado
  public BusinessException(ResponseStatus ResponseStatusCodes, String customMessage) {
    super(customMessage);
    this.ResponseStatusCodes = ResponseStatusCodes;
  }

  public ResponseStatus getResponseStatusCodes() {
    return ResponseStatusCodes;
  }
}