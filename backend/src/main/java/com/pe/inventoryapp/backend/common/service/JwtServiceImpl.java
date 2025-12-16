package com.pe.inventoryapp.backend.common.service;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtServiceImpl implements JwtService {

  // Extrae el id del usuario desde el JWT del header
  @Override
  public Long extractUserIdFromClaims(String header) {
    String token = header.replace("Bearer ", "");

    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = claims.get("id", Long.class);

    return id;
  }

}
