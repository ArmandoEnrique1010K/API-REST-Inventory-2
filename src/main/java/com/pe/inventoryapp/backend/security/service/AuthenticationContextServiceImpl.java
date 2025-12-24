package com.pe.inventoryapp.backend.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;

@Service
public class AuthenticationContextServiceImpl implements AuthenticationContextService {

  @Override
  public Long extractUserIdFromAuthentication(Authentication authentication) {

    if (authentication == null) {
      throw new BusinessException(ResponseStatusCodes.AUTH_FORBIDDEN, "El usuario no esta autenticado");
    }

    String userId = (String) authentication.getPrincipal();
    return Long.parseLong(userId);
  }

}
