package com.pe.inventoryapp.backend.common.service;

public interface JwtService {
  Long extractUserIdFromClaims(String header);
}
