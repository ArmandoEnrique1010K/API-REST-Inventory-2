package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.response.DataResponse;
import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;

public interface ResponseService {
  CommonResponse generateCommonResponse(String type, ResponseStatus code, String message);
  CommonResponse generateErrorResponse(ResponseStatus code, String message);
  CommonResponse generateSucessfullResponse(ResponseStatus code, String message); 
  ErrorWithFieldsResponse generateErrorWithFieldsResponse(ResponseStatus code, String message, Map<String, String> fields);
  DataResponse generateDataResponse(ResponseStatus code, Object data);
}
