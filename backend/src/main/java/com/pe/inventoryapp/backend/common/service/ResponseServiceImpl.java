package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.response.ErrorResponse;
import com.pe.inventoryapp.backend.common.response.SuccessfulResponse;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public SuccessfulResponse generateSuccessfulResponse(String type, String message) {
    SuccessfulResponse successfulResponse = new SuccessfulResponse();
    successfulResponse.setType(type);
    successfulResponse.setMessage(message);

    return successfulResponse;
  }

  @Override
  public ErrorResponse generateErrorResponse(String type, String message, Map<String, String> fields) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setType(type);
    errorResponse.setMessage(message);
    errorResponse.setFields(fields);

    return errorResponse;
  }
}
