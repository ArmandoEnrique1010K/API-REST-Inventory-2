package com.pe.inventoryapp.backend.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationContextServiceImpl implements AuthenticationContextService {

  @Override
  public Long extractUserIdFromAuthentication(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return Long.parseLong(userId);
  }

}
