package com.pe.inventoryapp.backend.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RequestValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleValidationException(RequestValidation ex) {
    ErrorWithFieldsResponse errorResponseWithFields = new ErrorWithFieldsResponse();
    errorResponseWithFields.setType("error_blank_fields");
    errorResponseWithFields.setMessage("Complete los campos faltantes");
    errorResponseWithFields.setFields(ex.getErrors());

    return ResponseEntity.badRequest().body(errorResponseWithFields);
  }

  @ExceptionHandler(FieldValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleGeneralException(FieldValidation ex) {
    ErrorWithFieldsResponse errorResponseWithFields = new ErrorWithFieldsResponse();
    errorResponseWithFields.setType("error_duplicate_data");
    errorResponseWithFields.setMessage("Error al guardar los datos (duplicación de datos)");

    Map<String, String> errors = new HashMap<>();
    errors.put(ex.getFieldName(), ex.getMessage());
    errorResponseWithFields.setFields(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseWithFields);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<CommonResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setType("error_invalid_id");
    commonResponse.setMessage("Error de tipo de dato " + ex);

    // Map<String, String> errors = new HashMap<>();
    // errors.put("id", "ID inválido, debe ser un número.");
    // errorResponse.setErrors(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<CommonResponse> handleRuntime(RuntimeException ex) {

    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setType("error_entity_not_found");
    commonResponse.setMessage("Error al obtener la entidad, " + ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonResponse);
  }

}
