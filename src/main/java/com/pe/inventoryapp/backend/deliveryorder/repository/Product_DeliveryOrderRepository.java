package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;

public interface Product_DeliveryOrderRepository extends JpaRepository<Product_DeliveryOrder, Long> {
  
  // Query para listar todos los productos disponibles registrados de una orden de entrega
  @Query("""
      SELECT p
      FROM Product_DeliveryOrder p
      WHERE p.deliveryOrder.id = :deliveryOrderId
      AND p.status = true
      ORDER BY p.product.entryDate ASC
    """)
  List<Product_DeliveryOrder> findAllByDeliveryOrderId(Long deliveryOrderId);

  // Verifica si existe una relacion entre un producto y una orden de entrega


  @Query("""
        SELECT COUNT(p) > 0
        FROM Product_DeliveryOrder p
        WHERE p.deliveryOrder.id = :deliveryOrderId
        AND p.product.id = :productId
        AND p.status = true
      """)
  boolean existsByDeliveryOrderIdAndProductId(Long deliveryOrderId, Long productId);

  // TODO: ESTO SE UTILIZA EN DELIVERYLINE, MODIFICAR EL QUERY
  @Query("""
        SELECT COUNT(p) > 0
        FROM Product_DeliveryOrder p
        WHERE p.id = :id
        AND p.deliveryOrder.id = :deliveryOrderId
        AND p.status = true
      """)
  boolean existsByIdAndDeliveryOrderId(Long id, Long deliveryOrderId);

}
