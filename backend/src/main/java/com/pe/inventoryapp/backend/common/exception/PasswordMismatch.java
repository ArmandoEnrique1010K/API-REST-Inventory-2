package com.pe.inventoryapp.backend.common.exception;

public class PasswordMismatch extends RuntimeException {
  public PasswordMismatch(String message) {
    super(message);
  }
}