package com.pe.inventoryapp.backend.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.response.DataResponse;
import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;

@Service
public class ResponseServiceImpl implements ResponseService {

  @Override
  public CommonResponse generateCommonResponse(String type, ResponseStatus code, String message) {
    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setType(type);
    commonResponse.setStatus(code.getStatus().value());
    
    if (message.isEmpty() || message == null || message.isBlank()) {
      message = code.getDefaultMessage();
    }

    commonResponse.setMessage(message);

    return commonResponse;
  }

  @Override
  public CommonResponse generateSucessfullResponse(ResponseStatus code, String message) {
    CommonResponse sucessfulResponse = new CommonResponse();

    // Tipo - Codigo HTTP - Tipo de respuesta - Mensaje
    sucessfulResponse.setType("success");
    sucessfulResponse.setStatus(code.getStatus().value());

    // Aqui debe mostrar la respuesta por defecto o el que se le pase
    // Normalmente si el mensaje es null o vacio, se muestra el mensaje por defecto
    if (message.isEmpty() || message == null || message.isBlank()) {
      message = code.getDefaultMessage();
    }

    sucessfulResponse.setMessage(message);

    return sucessfulResponse;
  }

  @Override
  public CommonResponse generateErrorResponse(ResponseStatus code, String message) {
    CommonResponse sucessfulResponse = new CommonResponse();
    sucessfulResponse.setType("error");
    sucessfulResponse.setStatus(code.getStatus().value());

    // Normalmente si el mensaje es null o vacio, se muestra el mensaje por defecto
    if (message.isEmpty() || message == null || message.isBlank()) {
      message = code.getDefaultMessage();
    }

    sucessfulResponse.setMessage(message);

    return sucessfulResponse;
  }

  @Override
  public ErrorWithFieldsResponse generateErrorWithFieldsResponse(ResponseStatus code, String message,
      Map<String, String> fields) {
    ErrorWithFieldsResponse errorWithFieldsResponse = new ErrorWithFieldsResponse();
    errorWithFieldsResponse.setType("error");
    errorWithFieldsResponse.setStatus(code.getStatus().value());

    if (message.isEmpty() || message == null || message.isBlank()) {
      message = code.getDefaultMessage();
    }

    errorWithFieldsResponse.setMessage(message);
    errorWithFieldsResponse.setFields(fields);
    return errorWithFieldsResponse;
  }

  @Override
  public DataResponse generateDataResponse(ResponseStatus code, Object data) {
    DataResponse dataResponse = new DataResponse();
    dataResponse.setType("success");
    dataResponse.setStatus(code.getStatus().value());
    dataResponse.setData(data);
    return dataResponse;
  }
}
