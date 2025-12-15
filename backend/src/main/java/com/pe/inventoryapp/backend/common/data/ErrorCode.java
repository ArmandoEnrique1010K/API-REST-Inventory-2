package com.pe.inventoryapp.backend.common.data;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Error de validación"),
  INVALID_ID(HttpStatus.BAD_REQUEST, "ID inválido"),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "Usuario no encontrado"),

  // 404
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entidad no encontrada"),

  // 409
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Recurso duplicado"),

  PASSWORD_REUSE_NOT_ALLOWED(HttpStatus.CONFLICT,
      "La contraseña no puede ser usada porque es la misma que la usada anteriormente"),
  PASSWORD_MISMATCH(HttpStatus.CONFLICT, "Las contraseñas no coinciden..."),
  // 500
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");

  private final HttpStatus status;
  private final String defaultMessage;

  ErrorCode(HttpStatus status, String defaultMessage) {
    this.status = status;
    this.defaultMessage = defaultMessage;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }

}
