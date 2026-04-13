package com.pe.inventoryapp.backend.deliveryline.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;

public interface DeliveryLineRepository
        extends JpaRepository<DeliveryLine, Long>, JpaSpecificationExecutor<DeliveryLine> {
    // Listar todas las lineas de entrega que pertenezcan a una orden de entrega
    // Omitiendo las lineas canceladas
    // @Query("""
    // SELECT dl FROM DeliveryLine dl
    // JOIN FETCH dl.model
    // JOIN FETCH dl.location l
    // JOIN FETCH l.subregion sr
    // JOIN FETCH sr.region
    // WHERE dl.deliveryOrder.id = :orderId AND dl.lineStatus != 'LINE_CANCELED'
    // """)
    // List<DeliveryLine> findAllByDeliveryOrderId(Long orderId);

    // Busqueda con filtros
    // @Query("""
    // SELECT d
    // FROM DeliveryLine d
    // JOIN d.model m
    // JOIN d.location l
    // JOIN l.subregion sr
    // JOIN sr.region r
    // JOIN d.deliveryOrder do
    // WHERE do.id = :deliveryOrderId
    // AND (:minRequiredQuantity IS NULL OR d.requiredQuantity >=
    // :minRequiredQuantity)
    // AND (:maxRequiredQuantity IS NULL OR d.requiredQuantity <=
    // :maxRequiredQuantity)
    // AND (:minLimitDate IS NULL OR d.limitDate >= :minLimitDate)
    // AND (:maxLimitDate IS NULL OR d.limitDate <= :maxLimitDate)
    // AND (:lineStatus IS NULL OR d.lineStatus = :lineStatus)
    // AND (:location IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :location,
    // '%')))
    // AND (:subregionId IS NULL OR sr.id = :subregionId)
    // AND (:regionId IS NULL OR r.id = :regionId)
    // AND (:modelId IS NULL OR d.model.id = :modelId)
    // AND d.lineStatus != 'LINE_CANCELED'
    // ORDER BY d.id DESC
    // """)
    // Page<DeliveryLine> searchAllByDeliveryOrderIdAndParams(
    // Long deliveryOrderId,
    // Integer minRequiredQuantity,
    // Integer maxRequiredQuantity,
    // LocalDateTime minLimitDate,
    // LocalDateTime maxLimitDate,
    // LineStatus lineStatus,
    // String location,
    // Long subregionId,
    // Long regionId,
    // Long modelId,
    // Pageable pageable);

    Optional<DeliveryLine> findByLocationId(Long idLocation);

    // Busqueda de la fecha de entrega mas cercana (prioridad de entrega de la linea
    // de entrega cuyo estado sea PENDING)
    @Query("""
              SELECT MIN(dl.limitDate)
              FROM DeliveryLine dl
              WHERE dl.deliveryOrder.id = :id
                AND dl.lineStatus IN ('LINE_PENDING', 'LINE_EXCEEDED')

              ORDER BY dl.limitDate ASC
            """)
    Optional<LocalDateTime> findClosestLimitDate(Long id);

    // @Query("""
    // SELECT COALESCE(SUM(dl.requiredQuantity), 0)
    // FROM DeliveryLine dl
    // JOIN dl.model m
    // JOIN dl.deliveryOrder do
    // WHERE do.id = :deliveryOrderId
    // AND m.id = :modelId
    // AND dl.lineStatus != 'LINE_CANCELED'
    // """)

    @Query("""
            SELECT COALESCE(SUM(dl.requiredQuantity), 0)
            FROM DeliveryLine dl
            WHERE dl.deliveryOrder.id = :deliveryOrderId
            AND dl.model.id = :modelId
            AND dl.lineStatus != 'LINE_CANCELED'
            """)
    Integer sumRequiredQuantityByDeliveryOrderIdAndModelId(
            @Param("deliveryOrderId") Long deliveryOrderId,
            @Param("modelId") Long modelId);

    @Query("""
                SELECT dl.location.subregion.region.id, COALESCE(SUM(dl.requiredQuantity), 0)
                FROM DeliveryLine dl
                WHERE dl.deliveryOrder.id = :deliveryOrderId
                  AND dl.lineStatus <> 'LINE_CANCELED'
                GROUP BY dl.location.subregion.region.id
            """)
    List<Object[]> sumRequiredGroupedByRegion(
            @Param("deliveryOrderId") Long deliveryOrderId);

    @Query("""
                SELECT dl.location.subregion.id, COALESCE(SUM(dl.requiredQuantity), 0)
                FROM DeliveryLine dl
                WHERE dl.deliveryOrder.id = :deliveryOrderId
                  AND dl.lineStatus <> 'LINE_CANCELED'
                GROUP BY dl.location.subregion.id
            """)
    List<Object[]> sumRequiredGroupedBySubregion(
            @Param("deliveryOrderId") Long deliveryOrderId);

    // Cuando verifica que no exista duplicado, tambien debe verificar que la linea
    // de entrega no este cancelada ('MOVEMENT_LINE_CANCELED' se considera como
    // eliminado, borrado lógico)
    @Query("""
                SELECT COUNT(dl) > 0
                FROM DeliveryLine dl
                WHERE dl.deliveryOrder.id = :deliveryOrderId
                  AND dl.model.id = :modelId
                  AND dl.location.id = :locationId
                  AND dl.lineStatus <> 'LINE_CANCELED'
            """)
    boolean existsDuplicate(
            @Param("deliveryOrderId") Long deliveryOrderId,
            @Param("modelId") Long modelId,
            @Param("locationId") Long locationId);

    @Query("""
                SELECT COUNT(dl) = 0
                FROM DeliveryLine dl
                WHERE dl.deliveryOrder.id = :deliveryOrderId
                  AND dl.lineStatus IN ('LINE_PENDING', 'LINE_EXCEEDED')
            """)
    boolean allLinesAreReady(@Param("deliveryOrderId") Long deliveryOrderId);

    @Query("""
            SELECT COUNT(dl) = 0
            FROM DeliveryLine dl
            WHERE dl.deliveryOrder.id = :deliveryOrderId
              AND dl.lineStatus != 'LINE_CANCELED'
            """)
    boolean allLinesAreCanceled(@Param("deliveryOrderId") Long deliveryOrderId);

    boolean existsByDeliveryOrderIdAndLineStatusIn(Long deliveryOrderId, List<LineStatus> statuses);

    @Query("""
            SELECT
              COUNT(dl),
              SUM(CASE WHEN dl.lineStatus = 'LINE_CANCELED' THEN 1 ELSE 0 END),
              SUM(CASE WHEN dl.lineStatus = 'LINE_READY' THEN 1 ELSE 0 END),
              SUM(CASE WHEN dl.lineStatus IN ('LINE_DELIVERED','LINE_MISSING') THEN 1 ELSE 0 END)
            FROM DeliveryLine dl
            WHERE dl.deliveryOrder.id = :orderId
            """)
    Object[] getStatusSummary(@Param("orderId") Long orderId);

    // |índice|significado|
    // |-----------|-----------------------------------|
    // |`result[0]`| total de líneas|
    // |`result[1]`| cuántas están canceladas|
    // |`result[2]`| cuántas están listas|
    // |`result[3]`| cuántas están entregadas o perdidas|

    // * RECORDAR QUE AQUI SE ESCRIBE EL NOMBRE DEL CAMPO QUE CONTIENE LA RELACION
    // @MANYTOONE QUE SE ENCUENTRA EN LA ENTIDAD DELIVERYORDER
    @EntityGraph(attributePaths = {
            "userCreator",
            "userUpdater",
            "model",
            "model.product",
            "model_DeliveryOrder",
            "deliveryOrder",
            "location",
            "location.subregion",
            "location.subregion.region"
    })
    // * IMPORTANTE: NO TOMA LA ENTIDAD "region", PARA AQUELLO SE HACE UN
    // FETCHRELATIONS EN EL SPECIFICATION (SOLAMENTE SI SE QUIERE ACCEDER AL SEGUNDO NIVEL DE RELACION DE ENTIDAD COMO EL CASO DE "region")

    @NonNull
    Page<DeliveryLine> findAll(
            @Nullable Specification<DeliveryLine> spec,
            @Nullable Pageable pageable);

    //
    // @EntityGraph(attributePaths = {
    // "location",
    // "userCreator",
    // "userUpdater",
    // "model",
    // "model.product",
    // "model_DeliveryOrder",
    // "deliveryOrder",
    // "location.subregion",
    // "location.subregion.region"
    // })
    // Optional<DeliveryLine> findFullById(Long id);

    @Query("""
                          SELECT dl FROM DeliveryLine dl
              JOIN FETCH dl.location l
              JOIN FETCH l.subregion s
              JOIN FETCH s.region r

              JOIN FETCH dl.model m
              JOIN FETCH m.product p
               JOIN FETCH p.category c
            JOIN FETCH p.type t


              JOIN FETCH dl.model_DeliveryOrder mdo
              JOIN FETCH dl.userCreator uc
              JOIN FETCH dl.userUpdater uu

              JOIN FETCH dl.deliveryOrder do
              WHERE dl.id = :id

              """)
    // * ES IMPOSIBLE QUITAR p.category y p.type PORQUE EL METODO DEL SERVICIO QUE
    // LO UTILIZARA ES DeliveryLineDetailsResponse Y AHI ESTA INCLUIDO LOS CAMPOS DE
    // CATEGORY Y TYPE, SI LO QUITO HARA UNAS 2 QUERIES ADICIONALES EN CONSOLA
    /*
     * JOIN FETCH p.category c
     * JOIN FETCH p.type t
     *
     * 
     */
    Optional<DeliveryLine> findByIdAndJoins(Long id);

}
