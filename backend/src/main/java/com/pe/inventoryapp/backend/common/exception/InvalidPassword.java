package com.pe.inventoryapp.backend.common.exception;

public class InvalidPassword extends RuntimeException {
  public InvalidPassword(String message) {
    super(message);
  }
}