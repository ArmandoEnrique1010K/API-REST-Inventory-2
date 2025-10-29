package com.pe.inventoryapp.backend.common.service;

import com.pe.inventoryapp.backend.common.response.Response;

public interface ResponseService {
  Response writeAResponse(String type, String message);
}
