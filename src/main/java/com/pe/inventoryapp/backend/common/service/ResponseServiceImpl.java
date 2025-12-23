package com.pe.inventoryapp.backend.common.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public CommonResponse generateCommonResponse(String type, ResponseStatusCodes code, String message) {
    CommonResponse successfulResponse = new CommonResponse();
    successfulResponse.setType(type);
    successfulResponse.setCode(code.name());
    successfulResponse.setMessage(message);

    return successfulResponse;
  }

}
