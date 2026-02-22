package com.pe.inventoryapp.backend.common.model.response;

public record CommonResponseWithSecretField(String type,
    int status,
    String message,
    String secretField) {
}
