package com.pe.inventoryapp.backend.common.model.response;

// Esta clase representa una respuesta que contiene datos, toda respuesta que devuelve datos es exitosa a pesar de no devolver ningun dato, es decir, un arreglo vacio
public record DataResponse<T>(
    String type,
    int status,
    T data) {
}
