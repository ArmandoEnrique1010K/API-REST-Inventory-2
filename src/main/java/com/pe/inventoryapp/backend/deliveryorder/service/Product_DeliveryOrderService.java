package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.Product_DeliveryOrderResponse;

public interface Product_DeliveryOrderService {
  void saveRelationProductInDeliveryOrder(Long idProduct, Long idDeliveryOrder);
  List<Product_DeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder);
  void deleteRelationProductDeliveryOrder(Long idDeliveryOrder);
}
