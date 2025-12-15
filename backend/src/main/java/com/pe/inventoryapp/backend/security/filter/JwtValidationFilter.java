package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

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

    String token = header.replace(PREFIX_TOKEN, "").trim();

    try {

      // 1. Parsear y validar el token
      Claims claims = Jwts.parser()
          .verifyWith((SecretKey) SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      // 2. Extraer username (subject)
      String username = claims.getSubject();
      // Long userId = claims.get("id", Long.class);
      // 3. Extraer roles (múltiples)

      // List<String> roles = claims.get("authorities", List.class);

      List<?> rolesObj = claims.get("authorities", List.class);
      List<String> roles = rolesObj == null ? List.of()
          : rolesObj.stream()
              .filter(Objects::nonNull)
              .map(Object::toString)
              .collect(Collectors.toList());

      if (roles.isEmpty()) {
        throw new JwtException("El token no contiene roles válidos");
      }

      // 4. Convertir roles a GrantedAuthority
      Collection<? extends GrantedAuthority> authorities = roles.stream()
          .map(SimpleGrantedAuthority::new)
          .toList();

      // 5. Crear el objeto de autenticación y establecerlo en el contexto de
      // seguridad
      // UserPrincipal principal = new UserPrincipal(userId, username);

      // UsernamePasswordAuthenticationToken authentication = new
      // UsernamePasswordAuthenticationToken(principal, null,
      // authorities);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
          authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 6. Continuar con el siguiente filtro
      chain.doFilter(request, response);

    } catch (

    JwtException e) {

      CommonResponse commonResponse = new CommonResponse();
      commonResponse.setCode("error");
      commonResponse.setMessage("El token JWT no es valido (error de token)");

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }

  }

}