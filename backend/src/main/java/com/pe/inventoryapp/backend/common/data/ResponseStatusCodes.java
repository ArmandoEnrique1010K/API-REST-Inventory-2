package com.pe.inventoryapp.backend.common.data;

import org.springframework.http.HttpStatus;

public enum ResponseStatusCodes {

        // 200
        SUCCESS_RESPONSE(HttpStatus.OK, "Se ha realizado la operación correctamente"),

        // 400
        VALIDATION_ERROR(
                        HttpStatus.BAD_REQUEST,
                        "Error de validación"),

        VALIDATION_INVALID_ID(
                        HttpStatus.BAD_REQUEST,
                        "ID inválido"),

        VALIDATION_PASSWORD_MISMATCH(
                        HttpStatus.BAD_REQUEST,
                        "Las contraseñas no coinciden"),

        // 401
        // Error en la autenticacion, correo o contraseña incorrecta
        AUTH_INVALID_CREDENTIALS(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"),

        // Token de 6 digitos invalido o expirado, vuelva a iniciar sesión
        AUTH_TOKEN_EXPIRED(
                        HttpStatus.UNAUTHORIZED,
                        "Token inválido o expirado"),

        // 403
        AUTH_FORBIDDEN(
                        HttpStatus.FORBIDDEN,
                        "No tiene permisos para realizar esta acción"),

        // 404
        USER_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"),

        ENTITY_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "Entidad no encontrada"),

        // 409
        // Recurso duplicado
        RESOURCE_DUPLICATE(
                        HttpStatus.CONFLICT,
                        "El recurso ya existe"),

        DEFAULT_RESOURCE(
                        HttpStatus.CONFLICT,
                        "Este recurso no se puede eliminar"),

        // La contraseña no puede ser usada porque es la misma que la anterior
        PASSWORD_REUSE_NOT_ALLOWED(
                        HttpStatus.CONFLICT,
                        "Contraseña no válida"),

        DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Recurso duplicado"),

        // 500
        // Servidor interno
        INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del sistema");

        private final HttpStatus status;
        private final String defaultMessage;

        ResponseStatusCodes(HttpStatus status, String defaultMessage) {
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
