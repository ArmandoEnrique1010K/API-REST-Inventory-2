package com.pe.inventoryapp.backend.common.exception;

public class ResourceNotFound extends RuntimeException {
  public ResourceNotFound(String message) {
    super(message);
  }
}