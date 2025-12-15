package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

public interface ResponseService {
  CommonResponse generateCommonResponse(String type, String message);

  // ErrorWithFieldsResponse generateErrorWithFieldsResponse(String type, String
  // message,
  // Map<String, String> fields);
}
