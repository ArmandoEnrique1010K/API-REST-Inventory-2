package com.pe.inventoryapp.backend.common.service;

import org.springframework.stereotype.Service;
import com.pe.inventoryapp.backend.common.response.Response;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public Response writeAResponse(String type, String message) {

    Response response = new Response();
    response.setType(type);
    response.setMessage(message);

    return response;
  }
}
