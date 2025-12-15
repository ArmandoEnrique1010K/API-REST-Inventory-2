package com.pe.inventoryapp.backend.common.exception;

public class InvalidToken extends RuntimeException {
  public InvalidToken(String message) {
    super(message);
  }
}