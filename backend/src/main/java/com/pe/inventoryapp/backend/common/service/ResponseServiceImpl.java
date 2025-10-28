package com.pe.inventoryapp.backend.common.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.dto.SuccessfulResponse;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public SuccessfulResponse writeAResponse(String type, String message) {

    SuccessfulResponse response = new SuccessfulResponse();
    response.setType(type);
    response.setMessage(message);

    return response;
  }
}
