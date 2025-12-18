package com.pe.inventoryapp.backend.common.exception;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;

public class BusinessException extends RuntimeException {
  private final ResponseStatusCodes ResponseStatusCodes;

  public BusinessException(ResponseStatusCodes ResponseStatusCodes) {
    super(ResponseStatusCodes.getDefaultMessage());
    this.ResponseStatusCodes = ResponseStatusCodes;
  }

  public BusinessException(ResponseStatusCodes ResponseStatusCodes, String customMessage) {
    super(customMessage);
    this.ResponseStatusCodes = ResponseStatusCodes;
  }

  public ResponseStatusCodes getResponseStatusCodes() {
    return ResponseStatusCodes;
  }
}