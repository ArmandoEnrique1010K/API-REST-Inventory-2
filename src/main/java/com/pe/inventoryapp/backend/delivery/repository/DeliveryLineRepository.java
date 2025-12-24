package com.pe.inventoryapp.backend.delivery.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;

public interface DeliveryLineRepository extends JpaRepository<DeliveryLine, Long> {

  // Page<DeliveryLine> findAllByDeliveryOrder(Pageable pageable, DeliveryOrder deliveryOrder);

  // Busqueda con filtros 
  @Query("""
      SELECT d
      FROM DeliveryLine d
      WHERE d.deliveryOrder.id = :deliveryOrderId
        AND (:minRequiredQuantity IS NULL OR d.requiredQuantity >= :minRequiredQuantity)
        AND (:maxRequiredQuantity IS NULL OR d.requiredQuantity <= :maxRequiredQuantity)
        AND (:minLimitDate IS NULL OR d.limitDate >= :minLimitDate)
        AND (:maxLimitDate IS NULL OR d.limitDate <= :maxLimitDate)
        AND (:preparationStatus IS NULL OR d.preparationStatus = :preparationStatus)
        AND (:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%')))
      """)
  Page<DeliveryLine> searchAllByDeliveryOrderIdAndParams(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDate minLimitDate, 
    LocalDate maxLimitDate, 
    PreparationStatus preparationStatus, 
    String location, 
    Pageable pageable);
}
