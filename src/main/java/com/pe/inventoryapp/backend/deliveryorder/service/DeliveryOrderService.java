package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;

public interface DeliveryOrderService {

  void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user);

  PageResponse<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String userClientName,
      OrderStatus status,
      Pageable pageable
    );

    PageResponse<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String userClientName,
      Pageable pageable
    );

  // TODO: DEFINIR UN METODO PARA OBTENER TODOS LOS USUARIOS QUE TENGAN EL ROL DE SOLAMENTE USER
  PageResponse<DeliveryOrderClientListResponse> findAllDeliveryOrdesByClientId(
      Long id,
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
      OrderStatus status,
      Pageable pageable
  );

  DeliveryOrderDetailsResponse findDeliveryOrderById(Long id);

  void changeLimitDate(Long id, LocalDateTime limitDate, Long id_user);

  void changeStatusOrderToCanceledById(Long id, Long id_user);
}
