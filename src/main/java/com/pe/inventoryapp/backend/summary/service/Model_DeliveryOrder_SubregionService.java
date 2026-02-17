package com.pe.inventoryapp.backend.summary.service;

import java.util.List;

import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_SubregionResponse;

public interface Model_DeliveryOrder_SubregionService {
    List<Model_DeliveryOrder_SubregionResponse> findAllByDeliveryOrderId(Long deliveryOrderId);
}
