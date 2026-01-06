package com.pe.inventoryapp.backend.common.model.response;

// Esta clase representa una respuesta común, que se puede utilizar tanto para
// respuestas exitosas como para respuestas erroneas
public record CommonResponse(
    String type,
    int status,
    String message) {
}
