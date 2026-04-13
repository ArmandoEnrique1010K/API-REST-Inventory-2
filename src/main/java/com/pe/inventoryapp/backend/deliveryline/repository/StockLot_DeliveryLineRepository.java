package com.pe.inventoryapp.backend.deliveryline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;

public interface StockLot_DeliveryLineRepository extends JpaRepository<StockLot_DeliveryLine, Long> {
  @Query("""
      SELECT sd FROM StockLot_DeliveryLine sd
      JOIN FETCH sd.deliveryLine d
      JOIN FETCH sd.stockLot s
      WHERE d.id = :deliveryLineId
      ORDER BY sd.createdAt DESC
      """)
  List<StockLot_DeliveryLine> findAllByDeliveryLineIdOrderByCreatedAtDesc(Long deliveryLineId);

  // List<StockLot_DeliveryLine> findAllByDeliveryLineId(Long deliveryLineId);
}
