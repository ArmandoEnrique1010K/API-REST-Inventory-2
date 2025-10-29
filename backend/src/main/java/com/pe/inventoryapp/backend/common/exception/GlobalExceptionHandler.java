package com.pe.inventoryapp.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pe.inventoryapp.backend.common.response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RequestValidation.class)
  public ResponseEntity<Response> handleValidationException(RequestValidation ex) {
    Response errorResponse = new Response();
    errorResponse.setType("error_blank_fields");
    errorResponse.setMessage("Complete los campos faltantes");
    // errorResponse.setErrors(ex.getErrors());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(FieldValidation.class)
  public ResponseEntity<Response> handleGeneralException(FieldValidation ex) {
    Response errorResponse = new Response();
    errorResponse.setType("error_duplicate_data");
    errorResponse.setMessage("Error al guardar los datos (duplicación de datos)");

    // Map<String, String> errors = new HashMap<>();
    // errors.put(ex.getFieldName(), ex.getMessage());
    // errorResponse.setErrors(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Response> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

    Response errorResponse = new Response();
    errorResponse.setType("error_invalid_id");
    errorResponse.setMessage("Error de tipo de dato");

    // Map<String, String> errors = new HashMap<>();
    // errors.put("id", "ID inválido, debe ser un número.");
    // errorResponse.setErrors(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

}
