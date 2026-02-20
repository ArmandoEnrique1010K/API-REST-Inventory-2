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

  // Listar todas las lineas de entrega que pertenezcan a una orden de entrega

  List<DeliveryLine> findAllByDeliveryOrderId(Long id);


  // Busqueda con filtros 
  @Query("""
      SELECT  d
      FROM DeliveryLine d
      JOIN d.model m
      JOIN d.location l
      JOIN l.subregion sr
      JOIN sr.region r
      JOIN d.deliveryOrder do
      WHERE do.id = :deliveryOrderId
        AND (:minRequiredQuantity IS NULL OR d.requiredQuantity >= :minRequiredQuantity)
        AND (:maxRequiredQuantity IS NULL OR d.requiredQuantity <= :maxRequiredQuantity)
        AND (:minLimitDate IS NULL OR d.limitDate >= :minLimitDate)
        AND (:maxLimitDate IS NULL OR d.limitDate <= :maxLimitDate)
        AND (:lineStatus IS NULL OR d.lineStatus = :lineStatus)
        AND (:location IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:subregionId IS NULL OR sr.id = :subregionId)
        AND (:regionId IS NULL OR r.id = :regionId)
        AND (:modelId IS NULL OR d.model.id = :modelId)
        AND d.lineStatus != 'CANCELED'
        ORDER BY l.id ASC  
      """)
  Page<DeliveryLine> searchAllByDeliveryOrderIdAndParams(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
    LocalDateTime maxLimitDate, 
    LineStatus lineStatus, 
    String location,
    Long subregionId,
    Long regionId,
    Long modelId,
    Pageable pageable);

  
  Optional<DeliveryLine> findByLocationId(Long idLocation);

  // Busqueda de la fecha de entrega mas cercana (prioridad de entrega de la linea de entrega cuyo estado sea PENDING)
  @Query("""
        SELECT MIN(dl.limitDate)
        FROM DeliveryLine dl
        WHERE dl.deliveryOrder.id = :id
          AND dl.lineStatus IN ('PENDING', 'EXCEEDED')

        ORDER BY dl.limitDate ASC
      """)
  Optional<LocalDateTime> findClosestLimitDate(Long id);


  @Query("""
          SELECT COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          JOIN dl.model m
          JOIN dl.deliveryOrder do
          WHERE do.id = :deliveryOrderId
          AND m.id = :modelId
          AND dl.lineStatus != 'CANCELED'
      """)
  Integer sumRequiredQuantityByDeliveryOrderIdAndModelId(
      @Param("deliveryOrderId") Long deliveryOrderId,
      @Param("modelId") Long modelId);

  @Query("""
          SELECT dl.location.subregion.region.id, COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          WHERE dl.deliveryOrder.id = :deliveryOrderId
            AND dl.lineStatus <> 'CANCELED'
          GROUP BY dl.location.subregion.region.id
      """)
  List<Object[]> sumRequiredGroupedByRegion(
      @Param("deliveryOrderId") Long deliveryOrderId);


  @Query("""
          SELECT dl.location.subregion.id, COALESCE(SUM(dl.requiredQuantity), 0)
          FROM DeliveryLine dl
          WHERE dl.deliveryOrder.id = :deliveryOrderId
            AND dl.lineStatus <> 'CANCELED'
          GROUP BY dl.location.subregion.id
      """)
  List<Object[]> sumRequiredGroupedBySubregion(
      @Param("deliveryOrderId") Long deliveryOrderId);


      // Cuando verifica que no exista duplicado, tambien debe verificar que la linea de entrega no este cancelada ('CANCELED' se considera como eliminado, borrado lógico)
      @Query("""
          SELECT COUNT(dl) > 0
          FROM DeliveryLine dl
          WHERE dl.deliveryOrder.id = :deliveryOrderId
            AND dl.model.id = :modelId
            AND dl.location.id = :locationId 
            AND dl.lineStatus <> 'CANCELED'
      """)
  boolean existsDuplicate(
      @Param("deliveryOrderId") Long deliveryOrderId,
      @Param("modelId") Long modelId,
      @Param("locationId") Long locationId);
  @Query("""
      SELECT COUNT(dl) = 0
      FROM DeliveryLine dl
      WHERE dl.deliveryOrder.id = :deliveryOrderId
        AND dl.lineStatus IN ('PENDING', 'EXCEEDED')
  """)
  boolean allLinesAreReady(@Param("deliveryOrderId") Long deliveryOrderId);


  @Query("""
      SELECT COUNT(dl) = 0
      FROM DeliveryLine dl
      WHERE dl.deliveryOrder.id = :deliveryOrderId
        AND dl.lineStatus != 'CANCELED'
      """)
  boolean allLinesAreCanceled(@Param("deliveryOrderId") Long deliveryOrderId);
}

