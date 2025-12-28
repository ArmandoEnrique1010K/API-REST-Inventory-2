package com.pe.inventoryapp.backend.security.exception;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException ex) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    // Esto se encuentra en el enum ResponseStatusCodes
        // UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "No estas autorizado para acceder a este recurso"),
    
        // TODO: ESTO SE PODRIA TRANSFORMAR EN UNA RESPUESTA PERSONALIZADA
    HttpStatus extractHttpStatus = ResponseStatusCodes.UNAUTHORIZED.getStatus();
    String extractMessage = ResponseStatusCodes.UNAUTHORIZED.getDefaultMessage();

    Map<String, Object> body = Map.of(
        "type", "error",
        // "status", HttpStatus.UNAUTHORIZED.value(),
        "error", 
        extractHttpStatus,
        "message", extractMessage
        // "path", request.getRequestURI()
        );
            response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
