package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderResponse;

public interface DeliveryOrderService {

  String save(DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  List<DeliveryOrderResponse> findAll();

  Page<DeliveryOrderResponse> findAllDeliveryOrdersByParams(
      Pageable pageable,
      PreparationStatus status,
      String createdByUser,
      String batch,
      Integer minQuantity,
      Integer maxQuantity,
      LocalDateTime startDate,
      LocalDateTime endDate);

  List<DeliveryOrderResponse> findAllByPreparationStatus(PreparationStatus status);

  Optional<DeliveryOrderResponse> findById(Long id);

  String update(Long id, DeliveryOrderRequest deliveryOrderRequest);

  void changePreparationStatus(Long id, PreparationStatus status);

  void verifyBatchExist(String batch);

}
