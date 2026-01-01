package com.pe.inventoryapp.backend.deliveryorder.service;

import com.pe.inventoryapp.backend.deliveryorder.model.request.Product_DeliveryOrderRequest;

public interface Product_DeliveryOrderService {
  void saveProduct_DeliveryOrder(Product_DeliveryOrderRequest product_DeliveryOrderRequest, Long idDeliveryOrder);
}
