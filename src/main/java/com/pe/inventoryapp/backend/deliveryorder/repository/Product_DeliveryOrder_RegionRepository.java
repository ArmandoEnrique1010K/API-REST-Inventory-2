package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder_Region;

public interface Product_DeliveryOrder_RegionRepository extends JpaRepository<Product_DeliveryOrder_Region, Long> {
  
  // Devuelve un Optional de Product_DeliveryOrder_Region si lo encuentra
  @Query("""
      SELECT pdr
      FROM Product_DeliveryOrder_Region pdr
      WHERE pdr.product_DeliveryOrder.id = :product_DeliveryOrderId AND pdr.region.id = :regionId
      """)

   Optional<Product_DeliveryOrder_Region> findByProduct_DeliveryOrderIdAndRegionId(Long product_DeliveryOrderId, Long regionId);


  // Devuelve una sumatoria de Product_DeliveryOrder_Region si lo encuentra
  @Query("""
      SELECT COALESCE(SUM(pdr.requiredTotalQuantity), 0)
      FROM Product_DeliveryOrder_Region pdr
      WHERE pdr.product_DeliveryOrder.product.id = :productId AND pdr.product_DeliveryOrder.deliveryOrder.id = :deliveryOrderId AND pdr.region.id = :regionId
      """)
   Integer sumRequiredTotalQuantityByProduct_DeliveryOrderIdAndRegionId(Long product_DeliveryOrderId, Long regionId);


   List<Product_DeliveryOrder_Region> findAllByProduct_DeliveryOrderId(Long productDeliveryOrderId);
}
