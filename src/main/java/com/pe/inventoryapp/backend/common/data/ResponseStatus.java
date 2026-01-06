package com.pe.inventoryapp.backend.common.data;

import org.springframework.http.HttpStatus;

public enum ResponseStatus {

		// 200 - OK
		// Se realizo una petición correcta 
		// GET con datos 
		// PUT o PATCH que actualiza y devuelve algo
		SUCCESS(
			HttpStatus.OK, 
			"Se ha realizado la operación correctamente"),

		// 201 - CREATED
		// Recurso creado
		// POST que devuelve algo
		CREATED(
			HttpStatus.CREATED, 
			"Se ha creado el recurso correctamente"),

		// 204 - NO CONTENT
		// No se devuelve nada
		// PUT o PATCH silencioso
		NO_CONTENT(
			HttpStatus.NO_CONTENT, 
			"Se ha realizado la operación correctamente"),

		// 400 - BAD_REQUEST
		// Datos incorrectos
		// Validaciones
		// JSON mal formado
		BAD_REQUEST(
			HttpStatus.BAD_REQUEST,
			"Se ha realizado una petición incorrecta, no se puede procesar la solicitud"),


		// TODO: CADA VEZ QUE SE DEVUELVA UN 401, DEBERA CERRAR SESION DE FORMA AUTOMATICA EN EL FRONTEND
	// 401 - UNAUTHORIZED
	// No autenticado
	// JWT ausente o invalido
	// Solo se utiliza en autenticación
	UNAUTHORIZED(
		HttpStatus.UNAUTHORIZED, 
		"No estas autorizado para acceder a este recurso"),

		// TODO: ELIMINAR ESTO
	// Token de 6 digitos invalido o expirado, vuelva a iniciar sesión
	TOKEN_EXPIRED(
			HttpStatus.BAD_REQUEST,
			"El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token"),


	// 403 - FORBIDDEN
	// Autenticado, pero sin permisos
	FORBIDDEN(
			HttpStatus.FORBIDDEN,
			"No tienes los permisos necesarios para realizar esta acción"),

	// 404 - NOT_FOUND
	NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"No se ha encontrado el recurso solicitado en el sistema"),

		// 409 - CONFLICT
	// Recurso duplicado

	CONFLICT(HttpStatus.CONFLICT, "No puedes realizar la operación con el recurso seleccionado"),

	DEFAULT_RESOURCE(
			HttpStatus.CONFLICT,
			"Este recurso no se puede ser modificado del sistema"),



	// 500 - INTERNAL_SERVER_ERROR
	// Servidor interno
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");





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
