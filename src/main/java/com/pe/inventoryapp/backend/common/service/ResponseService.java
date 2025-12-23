package com.pe.inventoryapp.backend.common.service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

public interface ResponseService {
  CommonResponse generateCommonResponse(String type, ResponseStatusCodes code, String message);
}
