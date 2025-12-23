package com.pe.inventoryapp.backend.common.exception;

public class FieldValidation extends RuntimeException {
  private final String fieldName;

  public FieldValidation(String fieldName, String message) {
    super(message);
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
