package com.pe.inventoryapp.backend.common.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// Esta clase representa una respuesta erronea, pero con campos asociados que
// representan los campos en donde se generaron los errores
public class ErrorWithFieldsResponse {
  private String code;
  private String message;
  private Map<String, String> fields;
}
