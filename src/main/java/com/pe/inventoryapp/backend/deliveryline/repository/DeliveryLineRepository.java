package com.pe.inventoryapp.backend.deliveryline.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;

public interface DeliveryLineRepository extends JpaRepository<DeliveryLine, Long> {

  // Busqueda con filtros 
  @Query("""
      SELECT  d
      FROM DeliveryLine d
      WHERE d.deliveryOrder.id = :deliveryOrderId
        AND (:minRequiredQuantity IS NULL OR d.requiredQuantity >= :minRequiredQuantity)
        AND (:maxRequiredQuantity IS NULL OR d.requiredQuantity <= :maxRequiredQuantity)
        AND (:minLimitDate IS NULL OR d.limitDate >= :minLimitDate)
        AND (:maxLimitDate IS NULL OR d.limitDate <= :maxLimitDate)
        AND (:lineStatus IS NULL OR d.lineStatus = :lineStatus)
        AND (:location IS NULL OR LOWER(d.location.name) LIKE LOWER(CONCAT('%', :location, '%')))
      """)
  Page<DeliveryLine> searchAllByDeliveryOrderIdAndParams(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
    LocalDateTime maxLimitDate, 
    LineStatus lineStatus, 
    String location, 
    Pageable pageable);

  
  Optional<DeliveryLine> findByLocationId(Long idLocation);

  List<DeliveryLine> findAllByDeliveryOrderId(Long idDeliveryOrder);

  @Query("""
        SELECT MIN(dl.limitDate)
        FROM DeliveryLine dl
        WHERE dl.deliveryOrder.id = :id
          AND dl.lineStatus = 'PENDING'
        ORDER BY dl.limitDate ASC
      """)
  Optional<LocalDateTime> findClosestLimitDate(Long id);


  @Query("""
          SELECT COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          JOIN dl.product p
          JOIN p.productDeliveryOrders pdo
          WHERE pdo.id = :productDeliveryOrderId
      """)
  Integer sumRequiredQuantityByProduct_DeliveryOrder(
      @Param("productDeliveryOrderId") Long productDeliveryOrderId);

  // TODO: VERIFICAR ESTE METODO
  @Query("""
          SELECT CASE WHEN COUNT(dl) > 0 THEN true ELSE false END
          FROM DeliveryLine dl
          JOIN dl.product p
          JOIN p.productDeliveryOrders pdo
          WHERE dl.location.id = :locationId
            AND pdo.id = :orderId
      """)
  boolean existsByLocationAndProductDeliveryOrder(
      @Param("locationId") Long locationId,
      @Param("orderId") Long orderId);

  @Query("""
      SELECT COUNT(dl) = 0
      FROM DeliveryLine dl
      WHERE dl.deliveryOrder.id = :deliveryOrderId
        AND dl.preparationStatus <> 'READY'
  """)
  boolean allLinesAreReady(@Param("deliveryOrderId") Long deliveryOrderId);


}

