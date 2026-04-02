package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.request.ModelRequest;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchFirstTenResponse;

public interface ModelService {
  void saveModelInProductId(ModelRequest modelRequest, MultipartFile file, Long productId);

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

  PageResponse<ModelListSearchResponse> searchAllModelsByName(
    Pageable pageable,
    String keyword
  );

  List<ModelListSearchFirstTenResponse> findFirstTenModelsByKeyword(String keyword);

  List<ModelListResponse> findAllModelsByProductId(Long productId);

  ModelDetailsResponse findModelById(Long id);

  void updateModelById(Long id, ModelRequest modelRequest, MultipartFile file);

  void changeStatusModelById(Long id);
}
