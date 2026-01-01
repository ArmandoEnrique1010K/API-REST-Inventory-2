package com.pe.inventoryapp.backend.deliveryorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;

public interface Product_DeliveryOrderRepository extends JpaRepository<Product_DeliveryOrder, Long> {
  
}
