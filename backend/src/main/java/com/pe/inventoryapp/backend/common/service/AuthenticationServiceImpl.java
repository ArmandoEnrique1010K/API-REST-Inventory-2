package com.pe.inventoryapp.backend.common.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  @Override
  public Long extractUserIdFromAuthentication(Authentication authentication) {
    String username = (String) authentication.getPrincipal();
    return Long.parseLong(username);
  }

}
