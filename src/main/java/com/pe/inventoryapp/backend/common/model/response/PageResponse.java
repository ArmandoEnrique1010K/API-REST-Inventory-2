package com.pe.inventoryapp.backend.common.model.response;

import java.util.List;

// Un record en Java es una clase inmutable y compacta diseñada para transportar datos
// Automaticamente genera un constructor completo, getters, equals, hashCode y toString
// Ideal para DTOs pero no sirve para entidades JPA
public record PageResponse<T>(
  List<T> content,
  int page,
  int size,
  long totalElements,
  int totalPages,
  boolean first,
  boolean last
){}