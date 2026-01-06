package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.ErrorWithFieldsResponse;

@Service
public class ResponseServiceImpl implements ResponseService {
  @Override
  public CommonResponse generateSucessfullResponse(ResponseStatus code, String message) {

    return new CommonResponse(
      "success",
        // Tipo - Codigo HTTP - Tipo de respuesta - Mensaje
      code.getStatus().value(),
        // Aqui debe mostrar la respuesta por defecto o el que se le pase
        // Normalmente si el mensaje es null o vacio, se muestra el mensaje por defecto
      message.isEmpty() || message == null || message.isBlank() ? code.getDefaultMessage() : message
    );
  }

  @Override
  public CommonResponse generateErrorResponse(ResponseStatus code, String message) {
    return new CommonResponse(
        "error",
        code.getStatus().value(),
        message.isEmpty() || message == null || message.isBlank() ? code.getDefaultMessage() : message);
  }

  @Override
  public ErrorWithFieldsResponse generateErrorWithFieldsResponse(ResponseStatus code, String message,
      Map<String, String> fields) {

    return new ErrorWithFieldsResponse(
        "error",
        code.getStatus().value(),
        message.isEmpty() || message == null || message.isBlank() ? code.getDefaultMessage() : message,
        fields
    );
  }

  // @Override
  // public DataResponse generateDataResponse(ResponseStatus code, Object data) {
  //   DataResponse dataResponse = new DataResponse();
  //   dataResponse.setType("success");
  //   dataResponse.setStatus(code.getStatus().value());
  //   dataResponse.setData(data);

  //   return dataResponse;
  // }

  @Override
  public <T> DataResponse<T> generateDataResponse(ResponseStatus code, T data) {
    return new DataResponse<>(
      "success",
      code.getStatus().value(),
      data
    );
  }

  @Override
  public CommonResponse generateCommonResponse(String type, ResponseStatus code, String message) {
    return new CommonResponse(
        type,
        code.getStatus().value(),
        message.isEmpty() || message == null || message.isBlank() ? code.getDefaultMessage() : message);
  }

}
