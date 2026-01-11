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
    """)
  List<Product_DeliveryOrder> findAllByDeliveryOrderId(Long deliveryOrderId);

  // Verifica si existe una relacion entre un producto y una orden de entrega
  boolean existsByDeliveryOrderIdAndProductId(Long deliveryOrderId, Long productId);
}
