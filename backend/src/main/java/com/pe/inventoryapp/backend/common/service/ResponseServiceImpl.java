package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public CommonResponse generateCommonResponse(String type, String message) {
    CommonResponse successfulResponse = new CommonResponse();
    successfulResponse.setCode(type);
    successfulResponse.setMessage(message);

    return successfulResponse;
  }

  // @Override
  // public ErrorWithFieldsResponse generateErrorWithFieldsResponse(String type,
  // String message,
  // Map<String, String> fields) {
  // ErrorWithFieldsResponse errorWithFieldsResponse = new
  // ErrorWithFieldsResponse();
  // errorWithFieldsResponse.setCode(type);
  // errorWithFieldsResponse.setMessage(message);
  // errorWithFieldsResponse.setFields(fields);

  // return errorWithFieldsResponse;
  // }
}
