package com.pe.inventoryapp.backend.deliveryline.service;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryline.model.response.StockLot_DeliveryLineResponse;

public interface StockLot_DeliveryLineService {
  List<StockLot_DeliveryLineResponse> findAllByDeliveryLineId(Long id);
}
