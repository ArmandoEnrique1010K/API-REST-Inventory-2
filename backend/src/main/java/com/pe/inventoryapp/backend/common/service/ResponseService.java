package com.pe.inventoryapp.backend.common.service;

import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

public interface ResponseService {
  CommonResponse generateCommonResponse(String type, ErrorCode code, String message);
}
