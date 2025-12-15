package com.pe.inventoryapp.backend.common.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pe.inventoryapp.backend.common.response.ErrorWithFieldsResponse;
import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RequestValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleValidationException(RequestValidation ex) {

    return buildFieldError(
        ErrorCode.VALIDATION_ERROR,
        "Complete los campos faltantes",
        ex.getErrors());

  }

  @ExceptionHandler(FieldValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleDuplicate(FieldValidation ex) {
    Map<String, String> errors = Map.of(ex.getFieldName(), ex.getMessage());

    return buildFieldError(
        ErrorCode.DUPLICATE_RESOURCE,
        "Error al guardar los datos (duplicación)",
        errors);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<CommonResponse> handleTypeMismatch() {

    return buildCommonError(
        ErrorCode.INVALID_ID,
        "ID inválido, debe ser un número");
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<CommonResponse> handleUserNotFound() {

    return buildCommonError(
        ErrorCode.USER_NOT_FOUND,
        "Usuario no encontrado");
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<CommonResponse> handleRuntime() {

    return buildCommonError(
        ErrorCode.ENTITY_NOT_FOUND,
        "Entidad no encontrada");
  }

  @ExceptionHandler(InvalidPassword.class)
  public ResponseEntity<CommonResponse> handleInvalidPasswrd() {
    return buildCommonError(
        ErrorCode.PASSWORD_REUSE_NOT_ALLOWED,
        "La contraseña no puede ser usada");

  }

  // Helpers auxiliares

  private ResponseEntity<ErrorWithFieldsResponse> buildFieldError(
      ErrorCode code,
      String message,
      Map<String, String> fields) {

    ErrorWithFieldsResponse response = new ErrorWithFieldsResponse();
    response.setCode(code.name());
    response.setMessage(message);
    response.setFields(fields);

    return ResponseEntity.status(code.getStatus()).body(response);
  }

  private ResponseEntity<CommonResponse> buildCommonError(
      ErrorCode code,
      String message) {

    CommonResponse response = new CommonResponse();
    response.setCode(code.name());
    response.setMessage(message);

    return ResponseEntity.status(code.getStatus()).body(response);
  }

}
