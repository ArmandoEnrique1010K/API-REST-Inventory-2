package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryorder.model.response.Product_DeliveryOrder_RegionResponse;

public interface Product_DeliveryOrder_RegionService {
  List<Product_DeliveryOrder_RegionResponse> findAllByDeliveryOrderId(Long deliveryOrderId);
}
