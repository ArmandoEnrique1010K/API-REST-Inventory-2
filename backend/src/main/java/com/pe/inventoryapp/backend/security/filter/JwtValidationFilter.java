package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.dto.ErrorResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtValidationFilter extends BasicAuthenticationFilter {

  public JwtValidationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String header = request.getHeader(HEADER_AUTHORIZATION);

    if (header == null || !header.startsWith(PREFIX_TOKEN)) {
      chain.doFilter(request, response);
      return;
    }

    String token = header.replace(PREFIX_TOKEN, "");

    try {

      Claims claims = Jwts.parser()
          .verifyWith((SecretKey) SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      Object authorityClaims = claims.get("authority");

      if (authorityClaims == null) {
        ErrorResponse errorResponseDto = new ErrorResponse();
        errorResponseDto.setType("error");
        errorResponseDto.setMessage("Token no válido o expirado");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponseDto));
        return;
      }

      String username = claims.getSubject();

      String authority = authorityClaims.toString();

      Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(authority));

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
          authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      chain.doFilter(request, response);
    } catch (JwtException e) {

      ErrorResponse errorResponseDto = new ErrorResponse();
      errorResponseDto.setType("error");
      errorResponseDto.setMessage("El token JWT no es valido");

      response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponseDto));
      response.setStatus(401);
      response.setContentType("application/json");
    }

  }

}