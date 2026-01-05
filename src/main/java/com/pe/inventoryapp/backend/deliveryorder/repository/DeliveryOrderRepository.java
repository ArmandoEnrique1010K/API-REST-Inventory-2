package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
  // Page<DeliveryOrder> findByPreparationStatus(PreparationStatus status);

  // Busca todas las ordenes que estan activas (estado Ready e InProgress)
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (d.preparationStatus = 'READY' OR d.preparationStatus = 'INPROGRESS')
          AND (:createdByUser IS NULL OR d.createdByUser LIKE CONCAT('%', :createdByUser, '%'))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND (
              (:startDate IS NULL OR :endDate IS NULL)
              OR d.limitDate BETWEEN :startDate AND :endDate
          )
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllActiveByParams(Pageable pageable,
      @Param("createdByUser") String createdByUser,
      @Param("batch") String batch,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // Busca todas las ordenes por los siguientes parametros:
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (:status IS NULL OR d.preparationStatus = :status)
          AND (:createdByUser IS NULL OR d.createdByUser LIKE CONCAT('%', :createdByUser, '%'))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND (
              (:startDate IS NULL OR :endDate IS NULL)
              OR d.limitDate BETWEEN :startDate AND :endDate
          )
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllByParams(
      Pageable pageable,
      @Param("status") OrderStatus status,
      @Param("createdByUser") String createdByUser,
      @Param("batch") String batch,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  Optional<DeliveryOrder> findByBatch(String batch);

  // DEFINIR UN MÉTODO PARA TRAER LAS LINES DE ENTREGA POR UNA ORDEN DE ENTREGA


}
