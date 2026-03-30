package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Type;
import com.pe.inventoryapp.backend.product.model.response.TypeResponse;

public class TypeMapper {
  private Type type;

  private TypeMapper() {

  }

  public static TypeMapper builder() {
    return new TypeMapper();
  }

  public TypeMapper setType(Type type) {
    this.type = type;
    return this;
  }

  public TypeResponse buildTypeListResponse() {

    if (type == null) {
      throw new RuntimeException("Debe pasar la entidad Type");
    }

    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new TypeResponse(
        type.getId(),
        type.getName(),
        type.isStatus()
      );
  }
}
