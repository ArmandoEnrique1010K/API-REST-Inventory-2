package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.CommonResponseWithSecretField;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.ErrorWithFieldsResponse;

public interface ResponseService {
  CommonResponse generateSucessfullResponse(ResponseStatus code, String message);

  CommonResponseWithSecretField generateSucessfullResponseWithSecretField(ResponseStatus code, String message, String secretField);

  CommonResponse generateErrorResponse(ResponseStatus code, String message);

  ErrorWithFieldsResponse generateErrorWithFieldsResponse(ResponseStatus code, String message,
      Map<String, String> fields);

  // <T> es un generico que permite recibir cualquier tipo de dato
  <T> DataResponse<T> generateDataResponse(ResponseStatus code, T data);

  CommonResponse generateCommonResponse(String type, ResponseStatus code, String message);
}
