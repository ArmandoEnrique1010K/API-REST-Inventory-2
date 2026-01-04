package com.pe.inventoryapp.backend.security.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
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

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException ex) throws IOException {
    CommonResponse commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, "");
    response.setStatus(commonResponse.getStatus());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
  }
}
