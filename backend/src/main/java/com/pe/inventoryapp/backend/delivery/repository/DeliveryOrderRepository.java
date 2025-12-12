package com.pe.inventoryapp.backend.delivery.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
  List<DeliveryOrder> findByPreparationStatus(PreparationStatus status);

  Optional<DeliveryOrder> findByBatch(String batch);
}
