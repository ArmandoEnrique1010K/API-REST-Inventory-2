package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import com.pe.inventoryapp.backend.common.response.ErrorResponse;
import com.pe.inventoryapp.backend.common.response.SuccessfulResponse;

public interface ResponseService {
  SuccessfulResponse generateSuccessfulResponse(String type, String message);

  ErrorResponse generateErrorResponse(String type, String message, Map<String, String> fields);
}
