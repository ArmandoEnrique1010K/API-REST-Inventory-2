package com.pe.inventoryapp.backend.stocklot.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {


  // Se reutiliza el último StockLot creado en las últimas 24h
  // para consolidar devoluciones
  Optional<StockLot> findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
      Long productId,
      LocalDateTime limitCreatedAt);

    // TODO: PODRIA SER NECESARIO ELIMINAR UNOS PARAMETROS DE LAS CONSULTAS DE BUSQUEDA
    // Query para la busqueda de lotes de stock para usuarios con el rol de SECRETARY
    // Devuelve una pagina de lotes de stock que cumplen con los criterios de busqueda
    @Query("""
            SELECT sl
            FROM StockLot sl
            WHERE 
            (:minQuantityReceived IS NULL OR sl.quantityReceived >= :minQuantityReceived)
            AND (:maxQuantityReceived IS NULL OR sl.quantityReceived <= :maxQuantityReceived)
            AND (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
            AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
            AND (:minQuantityDelivered IS NULL OR sl.quantityDelivered >= :minQuantityDelivered)
            AND (:maxQuantityDelivered IS NULL OR sl.quantityDelivered <= :maxQuantityDelivered)
            AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
            AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
            AND (:productName IS NULL OR LOWER(sl.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
            AND (:zeroStock IS NULL OR sl.zeroStock = :zeroStock)
            AND (:companyId IS NULL OR sl.company.id = :companyId)
            """)
    Page<StockLot> findAllByParams(
            @Param("minQuantityAvailable") Integer minQuantityAvailable,
            @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
            @Param("minQuantityReceived") Integer minQuantityReceived,
            @Param("maxQuantityReceived") Integer maxQuantityReceived,
            @Param("minQuantityDelivered") Integer minQuantityDelivered,
            @Param("maxQuantityDelivered") Integer maxQuantityDelivered,
            @Param("minCreatedAt") LocalDateTime minCreatedAt,
            @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
            @Param("productName") String productName,
            @Param("zeroStock") Boolean zeroStock,
            @Param("companyId") Long companyId,
            Pageable pageable);

    @Query("""
            SELECT sl
            FROM StockLot sl
            WHERE
            (:minQuantityReceived IS NULL OR sl.quantityReceived >= :minQuantityReceived)
            AND (:maxQuantityReceived IS NULL OR sl.quantityReceived <= :maxQuantityReceived)
            AND (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
            AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
            AND (:minQuantityDelivered IS NULL OR sl.quantityDelivered >= :minQuantityDelivered)
            AND (:maxQuantityDelivered IS NULL OR sl.quantityDelivered <= :maxQuantityDelivered)
            AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
            AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
            AND (:zeroStock IS NULL OR sl.zeroStock = :zeroStock)
            AND (:companyId IS NULL OR sl.company.id = :companyId)
            AND (sl.product.id = :productId)
            """)
    Page<StockLot> findAllByParamsAndProductId(
            @Param("minQuantityAvailable") Integer minQuantityAvailable,
            @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
            @Param("minQuantityReceived") Integer minQuantityReceived,
            @Param("maxQuantityReceived") Integer maxQuantityReceived,
            @Param("minQuantityDelivered") Integer minQuantityDelivered,
            @Param("maxQuantityDelivered") Integer maxQuantityDelivered,
            @Param("minCreatedAt") LocalDateTime minCreatedAt,
            @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
            @Param("zeroStock") Boolean zeroStock,
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            Pageable pageable);

    // Query para la busqueda de lotes de stock para usuarios con el rol de OPERATOR
    // Devuelve una pagina de lotes de stock que cumplen con los criterios de busqueda
    @Query("""
            SELECT sl
            FROM StockLot sl
            WHERE (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
            AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
            AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
            AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
            AND (:productName IS NULL OR LOWER(sl.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
            AND (sl.zeroStock = false)
            AND (:companyId IS NULL OR sl.company.id = :companyId)
            """)
    Page<StockLot> findAllByNotZeroStock(
            @Param("minQuantityAvailable") Integer minQuantityAvailable,
            @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
            @Param("minCreatedAt") LocalDateTime minCreatedAt,
            @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
            @Param("productName") String productName,
            @Param("companyId") Long companyId,
            Pageable pageable);

    // Query para la busqueda de lotes de stock para usuarios con el rol de OPERATOR
    // Devuelve una pagina de lotes de stock que cumplen con los criterios de
    // busqueda
    @Query("""
            SELECT sl
            FROM StockLot sl
            WHERE (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
            AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
            AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
            AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
            AND (sl.zeroStock = false)
            AND (sl.product.id = :productId)
            AND (:companyId IS NULL OR sl.company.id = :companyId)
          """)
    Page<StockLot> findAllByProductIdAndNotZeroStock(
            @Param("minQuantityAvailable") Integer minQuantityAvailable,
            @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
            @Param("minCreatedAt") LocalDateTime minCreatedAt,
            @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
            @Param("productId") Long productId,
            @Param("companyId") Long companyId,
            Pageable pageable);


 // METODO EN EL REPOSITORIO PARA LISTAR TODOS LOS LOTES DE STOCK QUE PERTENEZCAN AL MISMO PRODUCTO, PERO CON LA CONDICION DE QUE PIDA COMO REQUISITO UN ID DE LOTE DE STOCK Y TOME EL ID DEL PRODUCTO DE ESE LOTE DE STOCK PARA QUE LISTE TODOS LOS LOTES DE STOCK QUE CORRESPONDEN A ESE PRODUCTO, PERO QUE NO TOME EL MISMO LOTE DE STOCK QUE CORRESPONDA A ESE ID Y QUE TENGA EL VALOR false EN EL CAMPO ZEROSTOCK
        @Query("""
                SELECT sl
                FROM StockLot sl
                WHERE sl.id != :stockLotId
                AND sl.product.id = :productId
                AND sl.zeroStock = false
                """)
        List<StockLot> findAllByStockLotIdAndExcludeProductIdAndZeroStockIsFalse(Long stockLotId, Long productId);
        

    // Obtiene la sumatoria de todos los campos quantityReceived de un producto por
    // su id
    @Query("""
                        SELECT COALESCE(SUM(sl.quantityReceived), 0)
                        FROM StockLot sl
                        WHERE sl.product.id = :productId
                    """)
    Integer sumQuantityReceivedByProductId(@Param("productId") Long productId);

    // Obtiene la sumatoria de todos los campos quantityAvailable de un producto por su id
    @Query("""
                        SELECT COALESCE(SUM(sl.quantityAvailable), 0)
                        FROM StockLot sl
                        WHERE sl.product.id = :productId
                    """)
    Integer sumQuantityAvailableByProductId(@Param("productId") Long productId);

        @Query("""
                                SELECT COALESCE(SUM(sl.quantityDelivered), 0)
                                FROM StockLot sl
                                WHERE sl.product.id = :productId
                        """)
        Integer sumQuantityDeliveredByProductId(@Param("productId") Long productId);


}
