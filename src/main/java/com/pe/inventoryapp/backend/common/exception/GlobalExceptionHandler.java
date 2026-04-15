package com.pe.inventoryapp.backend.common.exception;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pe.inventoryapp.backend.common.service.ResponseService;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.ErrorWithFieldsResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private final ResponseService responseService;

  public GlobalExceptionHandler(ResponseService responseService) {
    this.responseService = responseService;
  }

  // Excepción de validación de campo
  @ExceptionHandler(RequestValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleValidationException(RequestValidation ex) {
    return buildFieldError(
        ResponseStatus.BAD_REQUEST,
        "Complete los campos faltantes",
        ex.getErrors());
  }

  // Excepción de duplicación de registro
  @ExceptionHandler(FieldValidation.class)
  public ResponseEntity<ErrorWithFieldsResponse> handleDuplicate(FieldValidation ex) {
    Map<String, String> errors = Map.of(ex.getFieldName(), ex.getMessage());

    return buildFieldError(
        ResponseStatus.CONFLICT,
        "Uno o más datos ya están registrados",
        errors);
  }

  // Excepción de tipo de dato ID inválido
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<CommonResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return buildCommonError(
        ResponseStatus.BAD_REQUEST,
        "Error en la URL al realizar la petición al servidor");
  }

  // Excepción de entidad no encontrada
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<CommonResponse> handleRuntime(RuntimeException ex) {
    log.error("Error en tiempo de ejecución : ", ex);

    return buildCommonError(
        ResponseStatus.INTERNAL_SERVER_ERROR,
        "Error interno del servidor, por favor vuelva a intentarlo");
  }

  // Excepción general
  @ExceptionHandler(Exception.class)
  public ResponseEntity<CommonResponse> handleException(Exception ex) {
    log.error("Error no controlado: ", ex);

    return buildCommonError(
        ResponseStatus.INTERNAL_SERVER_ERROR,
        "Error interno del servidor, por favor vuelva a intentarlo");
  }

  // Excepcion de @PreAuthorize (solamente se define esta excepcion si va a
  // existir @PreAuthorize en algun controlador)
  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<?> handleAuthDenied(AuthorizationDeniedException ex) {
    return buildCommonError(ResponseStatus.FORBIDDEN,
        "Acceso denegado, no tienes autorización para realizar la operación");
  }

  // Excepción de negocio personalizado
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<CommonResponse> handleBusiness(BusinessException ex) {
    ResponseStatus responseStatus = ex.getResponseStatusCodes();
    CommonResponse response = responseService.generateErrorResponse(responseStatus, ex.getMessage());
    HttpStatusCode status = Objects.requireNonNull(
        responseStatus.getStatus(),
        "HttpStatus no puede ser null");

    return ResponseEntity.status(status).body(response);
  }

  // Excepción de base de datos
  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<CommonResponse> handleDatabase(DataAccessException ex) {
    ResponseStatus responseStatus = ResponseStatus.CONFLICT;
    CommonResponse response = responseService.generateErrorResponse(responseStatus,
        "Se ha producido un error en la base de datos");
    HttpStatusCode status = Objects.requireNonNull(
        responseStatus.getStatus(),
        "HttpStatus no puede ser null");

    return ResponseEntity.status(status).body(response);
  }

  // Si hay un dato invalido
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<CommonResponse> handleInvalidDate(HttpMessageNotReadableException ex) {
    ResponseStatus responseStatus = ResponseStatus.CONFLICT;
    CommonResponse response = responseService.generateErrorResponse(responseStatus,
        "Uno de los datos introducidos no cumple el formato adecuado");
    HttpStatusCode status = Objects.requireNonNull(
        responseStatus.getStatus(),
        "HttpStatus no puede ser null");

    return ResponseEntity.status(status).body(response);
  }

  // MÉTODOS AUXILIARES

  // Construir una respuesta con errores de validación de campos
  private ResponseEntity<ErrorWithFieldsResponse> buildFieldError(
      ResponseStatus responseStatus,
      String message,
      Map<String, String> fields) {
    ErrorWithFieldsResponse response = responseService.generateErrorWithFieldsResponse(responseStatus, message, fields);
    HttpStatusCode status = Objects.requireNonNull(
        responseStatus.getStatus(),
        "HttpStatus no puede ser null");

    return ResponseEntity.status(status).body(response);
  }

  // Construir una respuesta con error de validación
  private ResponseEntity<CommonResponse> buildCommonError(
      ResponseStatus responseStatus,
      String message) {
    CommonResponse response = responseService.generateErrorResponse(responseStatus, message);
    HttpStatusCode status = Objects.requireNonNull(
        responseStatus.getStatus(),
        "HttpStatus no puede ser null");

    return ResponseEntity.status(status).body(response);
  }
}
