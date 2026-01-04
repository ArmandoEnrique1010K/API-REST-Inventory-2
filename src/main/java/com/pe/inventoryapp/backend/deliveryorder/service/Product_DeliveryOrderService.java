package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.request.Product_DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.ProductDeliveryOrderResponse;

public interface Product_DeliveryOrderService {
  void saveProduct_DeliveryOrder(Product_DeliveryOrderRequest product_DeliveryOrderRequest, Long idDeliveryOrder);
  List<ProductDeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder);
}
