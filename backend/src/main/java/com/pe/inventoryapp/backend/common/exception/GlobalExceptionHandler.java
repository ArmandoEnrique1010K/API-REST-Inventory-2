package com.pe.inventoryapp.backend.common.exception;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
        "Error de duplicación al guardar los datos",
        errors);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<CommonResponse> handleTypeMismatch() {

    return buildCommonError(
        ErrorCode.VALIDATION_INVALID_ID,
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
        "Ha ocurrido un error, la entidad no encontrada");
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<CommonResponse> handleBusiness(BusinessException ex) {

    ErrorCode code = ex.getErrorCode();

    CommonResponse response = new CommonResponse();
    response.setType("error");
    response.setCode(code.name());
    response.setMessage(ex.getMessage());

    return ResponseEntity.status(code.getStatus()).body(response);
  }

  // Error de base de datos
  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<?> handleDatabase(DataAccessException ex) {

    HttpStatus code = ErrorCode.INTERNAL_ERROR.getStatus();

    CommonResponse response = new CommonResponse();
    response.setType("error");
    response.setCode(code.name());
    response.setMessage("Se ha producido un error en la base de datos");

    return ResponseEntity.status(code).body(response);
  }

  // Helpers auxiliares

  private ResponseEntity<ErrorWithFieldsResponse> buildFieldError(
      ErrorCode code,
      String message,
      Map<String, String> fields) {

    ErrorWithFieldsResponse response = new ErrorWithFieldsResponse();
    response.setType("error");
    response.setCode(code.name());
    response.setMessage(message);
    response.setFields(fields);

    return ResponseEntity.status(code.getStatus()).body(response);
  }

  private ResponseEntity<CommonResponse> buildCommonError(
      ErrorCode code,
      String message) {

    CommonResponse response = new CommonResponse();
    response.setType("error");
    response.setCode(code.name());
    response.setMessage(message);

    return ResponseEntity.status(code.getStatus()).body(response);
  }

}
