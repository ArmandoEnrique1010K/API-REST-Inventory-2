package com.pe.inventoryapp.backend.security.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationContextService {
  Long extractUserIdFromAuthentication(Authentication authentication);
}
