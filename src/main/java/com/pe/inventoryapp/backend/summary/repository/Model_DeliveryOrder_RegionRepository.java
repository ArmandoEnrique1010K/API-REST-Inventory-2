package com.pe.inventoryapp.backend.summary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;

public interface Model_DeliveryOrder_RegionRepository extends JpaRepository<Model_DeliveryOrder_Region, Long> {
  @Query("""
      SELECT mdr
      FROM Model_DeliveryOrder_Region mdr
      WHERE mdr.model_DeliveryOrder.id = :model_DeliveryOrderId
      """)
  List<Model_DeliveryOrder_Region> findAllModel_DeliveryOrder_RegionsByModel_DeliveryOrderId(
      Long model_DeliveryOrderId);

  // Lista por id de model_DeliveryOrder y excluye aquellos cuya cantidad
  // requerida total sea 0
  @Query("""
      SELECT mdr
      FROM Model_DeliveryOrder_Region mdr
      WHERE mdr.model_DeliveryOrder.id = :model_DeliveryOrderId
      AND mdr.requiredTotalQuantity > 0
      """)
  List<Model_DeliveryOrder_Region> findAllByModel_DeliveryOrderIdAndRequiredTotalQuantityGreaterThanZero(
      Long model_DeliveryOrderId);

}
