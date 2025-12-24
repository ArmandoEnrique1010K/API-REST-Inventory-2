package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderListResponse;

public interface DeliveryOrderService {

  void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  Page<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
      Pageable pageable,
      PreparationStatus status,
      String createdByUser,
      String batch,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime startDate,
      LocalDateTime endDate);

  Page<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
      Pageable pageable,
      String createdByUser,
      String batch,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime startDate,
      LocalDateTime endDate);

  DeliveryOrderDetailsResponse findDeliveryOrderById(Long id);

  void updateDeliveryOrderById(Long id, DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  void changePreparationStatusDeliveryOrderById(Long id, PreparationStatus status);
}
