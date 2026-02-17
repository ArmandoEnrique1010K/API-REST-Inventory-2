package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;

public interface Model_DeliveryOrderRepository extends JpaRepository<Model_DeliveryOrder, Long> {
  
  // Query para listar todos los modelos de productos disponibles registrados de una orden de entrega
  @Query("""
      SELECT md
      FROM Model_DeliveryOrder md
      WHERE md.deliveryOrder.id = :deliveryOrderId
      AND md.status = true
      ORDER BY md.model.entryDate ASC
    """)
  List<Model_DeliveryOrder> findAllByDeliveryOrderId(Long deliveryOrderId);

  // Verifica si existe una relacion entre un modelo del producto y una orden de entrega
  @Query("""
        SELECT COUNT(md) > 0
        FROM Model_DeliveryOrder md
        WHERE md.deliveryOrder.id = :deliveryOrderId
        AND md.model.id = :modelId
        AND md.status = true
      """)
  boolean existsByDeliveryOrderIdAndModelId(Long deliveryOrderId, Long modelId);


  @Query("""
        SELECT md
        FROM Model_DeliveryOrder md
        WHERE md.deliveryOrder.id = :deliveryOrderId
        AND md.model.id = :modelId
        AND md.status = true
      """)
  Optional<Model_DeliveryOrder> findByModelIdAndDeliveryOrderId(Long modelId, Long deliveryOrderId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE Model_DeliveryOrder mdo
      SET mdo.requiredQuantityTotal =
          (
              SELECT COALESCE(SUM(dl.requiredQuantity), 0)
              FROM DeliveryLine dl
              WHERE dl.deliveryOrder.id = :deliveryOrderId
                AND dl.model.id = mdo.model.id
                AND dl.lineStatus <> 'CANCELED'
          )
      WHERE mdo.deliveryOrder.id = :deliveryOrderId
  """)
  void recalculateRequiredQuantities(@Param("deliveryOrderId") Long deliveryOrderId);
}
