package com.pe.inventoryapp.backend.security.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ResponseService responseService;

  public CustomAuthenticationEntryPoint(ResponseService responseService) {
    this.responseService = responseService;
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  // Excepcion personalizada cuando el usuario no esta autenticado o cuando el usuario esta deshabilitado y realiza una acción inmediatamente luego que fue deshabilitado por el administrador
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException ex) throws IOException {
        // Mensaje por defecto: No estas autorizado para acceder a este recurso
    CommonResponse commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, "");
    response.setStatus(commonResponse.getStatus());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
  }
}
