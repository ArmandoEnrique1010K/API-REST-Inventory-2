package com.pe.inventoryapp.backend.delivery.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
  // Page<DeliveryOrder> findByPreparationStatus(PreparationStatus status);

  // Busca todas las ordenes que estan activas (estado Ready e InProgress)
  // TODO: CORREGIR EL QUERY
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (d.preparationStatus = 'READY' OR d.preparationStatus = 'INPROGRESS')
          AND (:createdByUser IS NULL OR d.createdByUser LIKE CONCAT('%', :createdByUser, '%'))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND (:minQuantity IS NULL OR d.quantityTotal >= :minQuantity)
          AND (:maxQuantity IS NULL OR d.quantityTotal <= :maxQuantity)
          AND (
              (:startDate IS NULL OR :endDate IS NULL)
              OR d.limitDate BETWEEN :startDate AND :endDate
          )
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllActiveByParams(Pageable pageable,
      @Param("createdByUser") String createdByUser,
      @Param("batch") String batch,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // Busca todas las ordenes por los siguientes parametros:
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (:status IS NULL OR d.preparationStatus = :status)
          AND (:createdByUser IS NULL OR d.createdByUser LIKE CONCAT('%', :createdByUser, '%'))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND (:minQuantity IS NULL OR d.quantityTotal >= :minQuantity)
          AND (:maxQuantity IS NULL OR d.quantityTotal <= :maxQuantity)
          AND (
              (:startDate IS NULL OR :endDate IS NULL)
              OR d.limitDate BETWEEN :startDate AND :endDate
          )
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllByParams(
      Pageable pageable,
      @Param("status") PreparationStatus status,
      @Param("createdByUser") String createdByUser,
      @Param("batch") String batch,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  Optional<DeliveryOrder> findByBatch(String batch);

}
