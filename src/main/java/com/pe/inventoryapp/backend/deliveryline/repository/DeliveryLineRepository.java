package com.pe.inventoryapp.backend.deliveryline.repository;

import java.time.LocalDateTime;
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
        AND d.lineStatus != 'CANCELED'
        ORDER BY d.location.id ASC  
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

  // List<DeliveryLine> findAllByDeliveryOrderId(Long idDeliveryOrder);

  // Busqueda de la fecha de entrega mas cercana (prioridad de entrega de la linea de entrega cuyo estado sea PENDING)
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
          AND p.id = :productId
          AND dl.lineStatus != 'CANCELED'
      """)
  Integer sumRequiredQuantityByProduct_DeliveryOrder(
      @Param("productDeliveryOrderId") Long productDeliveryOrderId,
      @Param("productId") Long productId);

  @Query("""
        SELECT COALESCE(SUM(dl.requiredQuantity), 0)
        FROM DeliveryLine dl
        WHERE dl.product_DeliveryOrder.id = :pdoId
          AND dl.location.region.id = :regionId
          AND dl.lineStatus != 'CANCELED'
      """)
  Integer sumRequiredByProductDeliveryOrderAndRegion(
      @Param("pdoId") Long productDeliveryOrderId,
      @Param("regionId") Long regionId);



      // Cuando verifica que no exista duplicado, tambien debe verificar que la linea de entrega no este cancelada ('CANCELED' se considera como eliminado, borrado lógico)
      @Query("""
          SELECT COUNT(dl) > 0
          FROM DeliveryLine dl
          WHERE dl.deliveryOrder.id = :deliveryOrderId
            AND dl.product.id = :productId
            AND dl.location.id = :locationId 
            AND dl.lineStatus <> 'CANCELED'
      """)
  boolean existsDuplicate(
      @Param("deliveryOrderId") Long deliveryOrderId,
      @Param("productId") Long productId,
      @Param("locationId") Long locationId);
  @Query("""
      SELECT COUNT(dl) = 0
      FROM DeliveryLine dl
      WHERE dl.deliveryOrder.id = :deliveryOrderId
        AND dl.lineStatus != 'READY'
        AND dl.lineStatus != 'PENDING'
        AND dl.lineStatus != 'CANCELED'
  """)
  boolean allLinesAreReady(@Param("deliveryOrderId") Long deliveryOrderId);

}

