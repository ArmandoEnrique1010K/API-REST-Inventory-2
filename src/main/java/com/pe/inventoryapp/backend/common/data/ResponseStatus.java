package com.pe.inventoryapp.backend.common.data;

import org.springframework.http.HttpStatus;

public enum ResponseStatus {

		// 200 - OK
		SUCCESS_RESPONSE(HttpStatus.OK, "Se ha realizado la operación correctamente"),
	// 401 - UNAUTHORIZED
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "No estas autorizado para acceder a este recurso"),
	// Token de 6 digitos invalido o expirado, vuelva a iniciar sesión
	AUTH_TOKEN_EXPIRED(
			HttpStatus.UNAUTHORIZED,
			"El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token"),

		// 400 - BAD_REQUEST
		VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Los valores introducidos en los campos no tienen el formato adecuado"),

	BAD_REQUEST(
			HttpStatus.BAD_REQUEST,
			"Se ha realizado una petición incorrecta, no se puede procesar la solicitud"),

	// 403 - FORBIDDEN
	FORBIDDEN(
			HttpStatus.FORBIDDEN,
			"No tiene permisos necesarios para realizar esta acción"),

	// 404 - NOT_FOUND
	ENTITY_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"No se ha encontrado el recurso solicitado en el sistema"),

		// 409 - CONFLICT
	// Recurso duplicado

	CONFLICT(HttpStatus.CONFLICT, "No puedes realizar la operación con el recurso seleccionado"),

	DEFAULT_RESOURCE(
			HttpStatus.CONFLICT,
			"Este recurso no se puede ser modificado del sistema"),


		COMMON_ERROR(HttpStatus.BAD_REQUEST, "Ha ocurrido un error inesperado"), // BAD_REQUEST


	// 500 - INTERNAL_SERVER_ERROR
	// Servidor interno
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor"),




		// La contraseña no puede ser usada porque es la misma que la anterior
		PASSWORD_REUSE_NOT_ALLOWED(
										HttpStatus.CONFLICT,
										"Contraseña no válida");



		private final HttpStatus status;
		private final String defaultMessage;

		ResponseStatus(HttpStatus status, String defaultMessage) {
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
