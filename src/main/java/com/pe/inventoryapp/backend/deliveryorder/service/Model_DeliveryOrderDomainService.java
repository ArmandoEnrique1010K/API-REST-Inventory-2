package com.pe.inventoryapp.backend.deliveryorder.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;

@Service
public class Model_DeliveryOrderDomainService {
  private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;

  public Model_DeliveryOrderDomainService
  (Model_DeliveryOrderRepository model_DeliveryOrderRepository) {
    this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
  };

  public void recalculateSummaries(Long deliveryOrderId, Long modelId) {
    model_DeliveryOrderRepository
        .recalculateRequiredQuantities(deliveryOrderId, modelId);
  }

}
