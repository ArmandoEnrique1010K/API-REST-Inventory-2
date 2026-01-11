package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.ProductDeliveryOrderResponse;

public interface Product_DeliveryOrderService {
  void saveRelationProductInDeliveryOrder(Long idDeliveryOrder, Long idProduct);
  List<ProductDeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder);
  void deleteRelationProductDeliveryOrder(Long idDeliveryOrder);
}
