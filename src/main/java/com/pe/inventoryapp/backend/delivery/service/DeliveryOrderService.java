package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderListResponse;

public interface DeliveryOrderService {

  String save(DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  Page<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
      Pageable pageable,
      PreparationStatus status,
      String createdByUser,
      String batch,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime startDate,
      LocalDateTime endDate);

  Optional<DeliveryOrderDetailsResponse> findById(Long id);

  String update(Long id, DeliveryOrderRequest deliveryOrderRequest);

  void changePreparationStatus(Long id, PreparationStatus status);

  void verifyBatchExist(String batch);

}
