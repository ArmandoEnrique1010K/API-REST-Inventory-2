package com.pe.inventoryapp.backend.deliveryline.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;

public interface DeliveryLineRepository extends JpaRepository<DeliveryLine, Long> {

  // Page<DeliveryLine> findAllByDeliveryOrder(Pageable pageable, DeliveryOrder deliveryOrder);

  // Busqueda con filtros 
  @Query("""
      SELECT  d
      FROM DeliveryLine d
      WHERE d.deliveryOrder.id = :deliveryOrderId
        AND (:minRequiredQuantity IS NULL OR d.requiredQuantity >= :minRequiredQuantity)
        AND (:maxRequiredQuantity IS NULL OR d.requiredQuantity <= :maxRequiredQuantity)
        AND (:minLimitDate IS NULL OR d.limitDate >= :minLimitDate)
        AND (:maxLimitDate IS NULL OR d.limitDate <= :maxLimitDate)
        AND (:preparationStatus IS NULL OR d.preparationStatus = :preparationStatus)
        AND (:location IS NULL OR LOWER(d.location.name) LIKE LOWER(CONCAT('%', :location, '%')))
      """)
  Page<DeliveryLine> searchAllByDeliveryOrderIdAndParams(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
    LocalDateTime maxLimitDate, 
    PreparationStatus preparationStatus, 
    String location, 
    Pageable pageable);

  
  Optional<DeliveryLine> findByLocationId(Long idLocation);

  List<DeliveryLine> findAllByDeliveryOrderId(Long idDeliveryOrder);

  // Optional<LocalDateTime> findFirstByDeliveryOrderIdAndPreparationStatusOrderByLimitDateAsc(
  //   Long deliveryOrderId,
  //   PreparationStatus preparationStatus);

  @Query("""
        SELECT MIN(dl.limitDate)
        FROM DeliveryLine dl
        WHERE dl.deliveryOrder.id = :id
          AND dl.preparationStatus = 'INPROGRESS'
        ORDER BY dl.limitDate ASC
      """)
  Optional<LocalDateTime> findClosestLimitDate(Long id);



  @Query("""
          SELECT COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          WHERE dl.productDeliveryOrder.id = :product_DeliveryOrderId
      """)
  Integer sumRequiredQuantityByProduct_DeliveryOrder(Long product_DeliveryOrderId);

  boolean existsByLocationIdAndProductDeliveryOrderId(Long locationId, Long product_DeliveryOrderId);

  @Query("""
          SELECT COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          WHERE dl.deliveryOrder.id = :deliveryOrderId
      """)
  Integer sumRequiredQuantityByDeliveryOrderId(Long deliveryOrderId);

  @Query("""
      SELECT COUNT(dl) = 0
      FROM DeliveryLine dl
      WHERE dl.deliveryOrder.id = :deliveryOrderId
        AND dl.preparationStatus <> 'READY'
  """)
  boolean allLinesAreReady(@Param("deliveryOrderId") Long deliveryOrderId);
}
