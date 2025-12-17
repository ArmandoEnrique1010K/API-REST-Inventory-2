package com.pe.inventoryapp.backend.common.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationService {
  Long extractUserIdFromAuthentication(Authentication authentication);
}
