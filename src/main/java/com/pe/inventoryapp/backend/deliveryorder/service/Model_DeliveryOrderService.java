package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.Model_DeliveryOrderResponse;

public interface Model_DeliveryOrderService {
  void saveRelationModelInDeliveryOrder(Long idProduct, Long idDeliveryOrder);
  List<Model_DeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder);
  void deleteRelationModelDeliveryOrder(Long id);
}
