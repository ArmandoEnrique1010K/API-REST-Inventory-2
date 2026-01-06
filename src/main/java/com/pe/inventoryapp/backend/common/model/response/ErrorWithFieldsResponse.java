package com.pe.inventoryapp.backend.common.model.response;

import java.util.Map;

// Esta clase representa una respuesta erronea, pero con campos asociados que
// representan los campos en donde se generaron los errores
public record ErrorWithFieldsResponse(
    String type,
    int status,
    String message,
    Map<String, String> fields) {
}
