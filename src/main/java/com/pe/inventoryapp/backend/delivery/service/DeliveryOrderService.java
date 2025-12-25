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
      PreparationStatus status,
      String createdByUser,
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
            Pageable pageable
    );

  Page<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
      String createdByUser,
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
          Pageable pageable
    );

  DeliveryOrderDetailsResponse findDeliveryOrderById(Long id);

  void updateDeliveryOrderById(Long id, DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  void changePreparationStatusDeliveryOrderById(Long id, PreparationStatus status, Long id_user);
}
