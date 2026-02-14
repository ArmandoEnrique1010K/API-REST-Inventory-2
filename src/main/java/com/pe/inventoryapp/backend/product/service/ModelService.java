package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.request.ModelRequest;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;

public interface ModelService {
  void saveModelInProductId(ModelRequest modelRequest, Long productId);

  PageResponse<ModelListResponse> searchAllModelsByParams(
      Pageable pageable,
      String keyword,
      Integer minStock,
      Integer maxStock,
      LocalDate minEntryDate,
      LocalDate maxEntryDate,
      Boolean status,
      Long categoryId,
      Long typeId);

  ModelDetailsResponse findModelById(Long id);

  void updateModelById(Long id, ModelRequest modelRequest);

  void changeStatusModelById(Long id);
}
