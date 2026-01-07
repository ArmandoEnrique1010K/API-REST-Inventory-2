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
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtValidationFilter extends BasicAuthenticationFilter {

  private final ResponseService responseService;
  private final UserRepository userRepository;

  public JwtValidationFilter(AuthenticationManager authenticationManager, ResponseService responseService, UserRepository userRepository) {
    super(authenticationManager);
    this.responseService = responseService;
    this.userRepository = userRepository;
  }

  private String getTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null)
      return null;

    for (Cookie cookie : request.getCookies()) {
      if ("ACCESS_TOKEN".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = getTokenFromCookie(request);

    if (token == null) {
      chain.doFilter(request, response);
      return;
    }

    try {

      // 1. Parsear y validar el token
      Claims claims = Jwts.parser()
          .verifyWith((SecretKey) SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      // 2. Extraer username (subject)
      String userId = claims.getSubject(); // en tu caso: id_user como String
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


      // 3.5 Extraer el usuario
        User user = userRepository.findById(Long.parseLong(userId))
          .orElseThrow(() -> new BusinessException(ResponseStatus.UNAUTHORIZED, "El usuario no existe"));

        // Si el usuario no esta activo, se eliminara la sesion
        if (!user.isActive()) {
          SecurityContextHolder.clearContext();

          Cookie cookie = new Cookie("ACCESS_TOKEN", null);
          cookie.setMaxAge(0);
          cookie.setHttpOnly(true);
          cookie.setPath("/");

          response.addCookie(cookie);
          // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "El usuario no esta activo");

          return;
        }

      // 4. Convertir roles a GrantedAuthority
      Collection<? extends GrantedAuthority> authorities = roles.stream()
          .map(SimpleGrantedAuthority::new)
          .toList();

      // 5. Crear el objeto de autenticación y establecerlo en el contexto de
      // username es el email
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userId, null,
          authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 6. Continuar con el siguiente filtro
      chain.doFilter(request, response);

    } catch (

    JwtException e) {

      // Token no valido
      CommonResponse commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, 
        "El token JWT no es valido (error de token)");

      response.setStatus(commonResponse.status());
      response.setContentType("application/json");
      response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }
  }
}