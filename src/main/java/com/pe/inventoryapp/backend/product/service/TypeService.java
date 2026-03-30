package com.pe.inventoryapp.backend.product.service;

import java.util.List;

import com.pe.inventoryapp.backend.product.model.request.TypeRequest;
import com.pe.inventoryapp.backend.product.model.response.TypeResponse;

public interface TypeService {
  void saveType(TypeRequest typeRequest);

  List<TypeResponse> listAllTypes();
  List<TypeResponse> findAllActiveTypes();

  TypeResponse findTypeById(Long id);

  void updateTypeById(Long id, TypeRequest typeRequest);

  void changeStatusTypeById(Long id);
}
