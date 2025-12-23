package com.pe.inventoryapp.backend.delivery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;

public interface DeliveryLineRepository extends JpaRepository<DeliveryLine, Long> {

  Page<DeliveryLine> findAllByDeliveryOrder(Pageable pageable, DeliveryOrder deliveryOrder);

}
