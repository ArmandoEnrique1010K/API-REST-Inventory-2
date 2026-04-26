package com.pe.inventoryapp.backend.deliveryorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;

public interface DeliveryOrderRepository
        extends JpaRepository<DeliveryOrder, Long>, JpaSpecificationExecutor<DeliveryOrder> {
    // Page<DeliveryOrder> findByPreparationStatus(PreparationStatus status);

    // TODO: EN UNA FUTURA ACTUALIZACION PODRIA ELEGIR LA FECHA LIMITE EN UN UNICO
    // PARAMETRO PARA QUE LO BUSQUE
    // POR UN DIA Y NO POR UN RANGO DE DIAS
    // Busca todas las ordenes por los siguientes parametros (PARA SECRETARIOS Y
    // ADMINISTRADORES)

    // @Query("""
    // SELECT d
    // FROM DeliveryOrder d
    // WHERE (:status IS NULL OR d.orderStatus = :status)
    // AND (:userClientName IS NULL
    // OR LOWER(d.userClient.firstname) LIKE LOWER(CONCAT('%', :userClientName,
    // '%'))
    // OR LOWER(d.userClient.lastname) LIKE LOWER(CONCAT('%', :userClientName,
    // '%')))
    // AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
    // AND (:startDate IS NULL OR d.priorityDate >= :startDate)
    // AND (:endDate IS NULL OR d.priorityDate <= :endDate)
    // AND d.orderStatus != 'ORDER_CANCELED'
    // ORDER BY d.createdAt DESC
    // """)
    // Page<DeliveryOrder> findAllByParams(
    // Pageable pageable,
    // @Param("batch") String batch,
    // @Param("startDate") LocalDateTime startDate,
    // @Param("endDate") LocalDateTime endDate,
    // @Param("status") OrderStatus status,
    // @Param("userClientName") String userClientName);

    // Busca todas las ordenes que estan activas (estado Ready e InProgress) (PARA
    // OPERADORES)
    // Recordar que debe tomar un rango de fechas

    // @Query("""
    // SELECT d
    // FROM DeliveryOrder d
    // WHERE (d.orderStatus = 'ORDER_READY' OR d.orderStatus = 'ORDER_PENDING')
    // AND (:userClientName IS NULL
    // OR LOWER(d.userClient.firstname) LIKE LOWER(CONCAT('%', :userClientName,
    // '%'))
    // OR LOWER(d.userClient.lastname) LIKE LOWER(CONCAT('%', :userClientName,
    // '%')))
    // AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
    // AND (:startDate IS NULL OR d.priorityDate >= :startDate)
    // AND (:endDate IS NULL OR d.priorityDate <= :endDate)
    // ORDER BY d.createdAt DESC
    // """)
    // Page<DeliveryOrder> findAllActiveByParams(
    // Pageable pageable,
    // @Param("batch") String batch,
    // @Param("startDate") LocalDateTime startDate,
    // @Param("endDate") LocalDateTime endDate,
    // @Param("userClientName") String userClientName);

    // Busca todas las ordenes por id del cliente y parametros (PARA CLIENTES EN
    // GENERAL Y USUARIOS QUE NO SON EMPLEADOS)

    // Este método no debe listar todas las ordenes que tengan el estado
    // MOVEMENT_LINE_CANCELED
    // @Query("""
    // SELECT d
    // FROM DeliveryOrder d
    // WHERE d.userClient.id = :userId
    // AND d.orderStatus != 'ORDER_CANCELED'
    // AND (:status IS NULL OR d.orderStatus = :status)
    // AND (:batch IS NULL OR d.batch LIKE CONCAT('%', :batch, '%'))
    // AND (:startDate IS NULL OR d.priorityDate >= :startDate)
    // AND (:endDate IS NULL OR d.priorityDate <= :endDate)
    // ORDER BY d.createdAt DESC
    // """)

    // Page<DeliveryOrder> findAllByUserClientId(
    // Pageable pageable,
    // Long userId,
    // @Param("batch") String batch,
    // @Param("startDate") LocalDateTime startDate,
    // @Param("endDate") LocalDateTime endDate,
    // @Param("status") OrderStatus status);

    // Busca una orden por su batch
    // Optional<DeliveryOrder> findByBatch(String batch);

    // * RECORDAR QUE AQUI SE ESCRIBE EL NOMBRE DEL CAMPO QUE CONTIENE LA RELACION
    // @MANYTOONE QUE SE ENCUENTRA EN LA ENTIDAD DELIVERYORDER
    @EntityGraph(attributePaths = {
            "userCreator",
            "userUpdater",
            "userClient"
    })
    @NonNull
    Page<DeliveryOrder> findAll(
            @Nullable Specification<DeliveryOrder> spec,
            @Nullable Pageable pageable);

    @Query("""
            SELECT do FROM DeliveryOrder do
                      JOIN FETCH do.userCreator uc
                      JOIN FETCH do.userUpdater uu
                      JOIN FETCH do.userClient ul
                      WHERE do.id = :id

            """)
    Optional<DeliveryOrder> findByIdAndJoins(Long id);

    @Query("""
                SELECT COUNT(do) FROM DeliveryOrder do
                WHERE do.orderStatus = 'ORDER_PENDING'
            """)
    long countByOrderStatusPending();

    @Query("""
                SELECT COUNT(do) FROM DeliveryOrder do
                WHERE do.orderStatus = 'ORDER_PENDING'
                AND do.userClient.id = :id
            """)
    long countByOrderStatusPendingAndUser(Long id);

    // @Query("""
    // SELECT COALESCE(
    // (
    // SUM(
    // CASE
    // WHEN dl.lineStatus IN (
    // 'LINE_MISSING', 'LINE_READY', 'LINE_DELIVERED', 'LINE_CANCELED'
    // )
    // THEN 1
    // ELSE 0
    // END
    // ) * 100.0
    // ) / COUNT(dl)
    // , 0)
    // FROM DeliveryLine dl
    // WHERE dl.deliveryOrder.id = :deliveryOrderId
    // """)
    // Double percentageByDeliveryOrder(Long deliveryOrderId);

    @Query("""
            SELECT COALESCE(
                (
                    SUM(
                        CASE
                            WHEN dl.lineStatus IN (
                                'LINE_READY','LINE_DELIVERED','LINE_MISSING'
                            ) THEN 1
                            ELSE 0
                        END
                    ) * 100.0
                ) /
                NULLIF(
                    SUM(
                        CASE
                            WHEN dl.lineStatus <> 'LINE_CANCELED' THEN 1
                            ELSE 0
                        END
                    ),0
                )
            ,0)
            FROM DeliveryLine dl
            WHERE dl.deliveryOrder.id = :deliveryOrderId
            """)
    Double percentageByDeliveryOrder(Long deliveryOrderId);

    // @Query("""
    // SELECT new
    // com.pe.inventoryapp.backend.dashboard.model.dto.PendingDeliveryOrdersDto(
    // d.id,
    // d.batch,
    // d.priorityDate,
    // COALESCE(
    // (
    // SUM(
    // CASE
    // WHEN dl.lineStatus IN (
    // 'LINE_MISSING',
    // 'LINE_READY',
    // 'LINE_DELIVERED',
    // 'LINE_CANCELED'
    // )
    // THEN 1
    // ELSE 0
    // END
    // ) * 100.0
    // ) / COUNT(dl)
    // , 0.0)

    // )
    // FROM DeliveryOrder d
    // JOIN d.deliveryLines dl
    // WHERE d.orderStatus = 'ORDER_PENDING'
    // GROUP BY d.id, d.batch, d.priorityDate
    // ORDER BY d.priorityDate ASC
    // """)
    // List<PendingDeliveryOrdersDto> summaryDeliveryOrdersPending(Pageable
    // pageable);

    @Query("""
            SELECT do FROM DeliveryOrder do
            WHERE do.orderStatus = 'ORDER_PENDING'
            """)
    List<DeliveryOrder> summaryDeliveryOrderPending(Pageable pageable);

    @Query("""
            SELECT do FROM DeliveryOrder do
            JOIN FETCH do.userClient uc
            WHERE do.orderStatus = 'ORDER_PENDING' AND uc.id = :userId
            """)
    List<DeliveryOrder> summaryDeliveryOrderPendingByUser(Pageable pageable, Long userId);



    // @Query("""
    // SELECT new
    // com.pe.inventoryapp.backend.dashboard.model.dto.PendingDeliveryOrdersDto(
    // d.id,
    // d.batch,
    // d.priorityDate,
    // COALESCE(
    // (
    // SUM(
    // CASE
    // WHEN dl.lineStatus IN (
    // 'LINE_MISSING',
    // 'LINE_READY',
    // 'LINE_DELIVERED',
    // 'LINE_CANCELED'
    // )
    // THEN 1
    // ELSE 0
    // END
    // ) * 100.0
    // ) / COUNT(dl)
    // , 0.0)

    // )
    // FROM DeliveryOrder d
    // JOIN d.deliveryLines dl
    // WHERE d.orderStatus = 'ORDER_PENDING' AND d.userClient.id = :id
    // GROUP BY d.id, d.batch, d.priorityDate
    // ORDER BY d.priorityDate ASC
    // """)
    // List<PendingDeliveryOrdersDto> summaryDeliveryOrdersPendingAndUser(Long id,
    // Pageable pageable);

}
