package com.pe.inventoryapp.backend.security.exception;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

  private final ResponseService responseService;

  public CustomAccessDeniedHandler(ResponseService responseService) {
    this.responseService = responseService;
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    CommonResponse commonResponse = responseService.generateErrorResponse(ResponseStatus.FORBIDDEN, "");
    response.setStatus(commonResponse.getStatus());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
  }
}