package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
  // Page<DeliveryOrder> findByPreparationStatus(PreparationStatus status);

  // TODO: PODRIA ELEGIR LA FECHA LIMITE EN UN UNICO PARAMETRO PARA QUE LO BUSQUE
  // POR UN DIA Y NO POR UN RANGO DE DIAS
  // Busca todas las ordenes por los siguientes parametros (PARA SECRETARIOS Y
  // ADMINISTRADORES)
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (:status IS NULL OR d.orderStatus = :status)
          AND (:userClientName IS NULL
          OR LOWER(d.userClient.firstname) LIKE LOWER(CONCAT('%', :userClientName, '%'))
          OR LOWER(d.userClient.lastname) LIKE LOWER(CONCAT('%', :userClientName, '%')))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND ((:startDate IS NULL OR :endDate IS NULL) OR d.priorityDate BETWEEN :startDate AND :endDate)
          AND d.orderStatus != 'CANCELED'
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllByParams(
      Pageable pageable,
      @Param("batch") String batch,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("status") OrderStatus status,
      @Param("userClientName") String userClientName);

  // Busca todas las ordenes que estan activas (estado Ready e InProgress) (PARA
  // OPERADORES)
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE (d.orderStatus = 'READY' OR d.orderStatus = 'PENDING')
          AND (:userClientName IS NULL
          OR LOWER(d.userClient.firstname) LIKE LOWER(CONCAT('%', :userClientName, '%'))
          OR LOWER(d.userClient.lastname) LIKE LOWER(CONCAT('%', :userClientName, '%')))
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND ((:startDate IS NULL OR :endDate IS NULL) OR d.limitDate BETWEEN :startDate AND :endDate)
        ORDER BY d.createdAt DESC
      """)
  Page<DeliveryOrder> findAllActiveByParams(
      Pageable pageable,
      @Param("batch") String batch,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("userClientName") String userClientName);

  // Busca todas las ordenes por id del cliente y parametros (PARA CLIENTES EN
  // GENERAL Y USUARIOS QUE NO SON EMPLEADOS)

  // Este método no debe listar todas las ordenes que tengan el estado CANCELED
  @Query("""
        SELECT d
        FROM DeliveryOrder d
        WHERE d.userClient.id = :userId
          AND d.orderStatus != 'CANCELED'
          AND (:status IS NULL OR d.orderStatus = :status)
          AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
          AND ((:startDate IS NULL OR :endDate IS NULL) OR d.limitDate BETWEEN :startDate AND :endDate)
        ORDER BY d.createdAt DESC
      """)

  Page<DeliveryOrder> findAllByUserClientId(
      Pageable pageable,
      Long userId,
      @Param("batch") String batch,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("status") OrderStatus status);

  // Busca una orden por su batch
  // Optional<DeliveryOrder> findByBatch(String batch);
}
