package com.pe.inventoryapp.backend.common.service;

import com.pe.inventoryapp.backend.common.dto.SuccessfulResponse;

public interface ResponseService {
  SuccessfulResponse writeAResponse(String type, String message);
}
