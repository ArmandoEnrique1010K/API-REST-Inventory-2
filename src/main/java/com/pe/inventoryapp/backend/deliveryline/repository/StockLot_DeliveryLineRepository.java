package com.pe.inventoryapp.backend.deliveryline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;

public interface StockLot_DeliveryLineRepository extends JpaRepository<StockLot_DeliveryLine, Long> {
  List<StockLot_DeliveryLine> findAllByDeliveryLineId(Long id);
}
