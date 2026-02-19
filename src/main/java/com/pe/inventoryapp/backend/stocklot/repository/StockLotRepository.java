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

  // TODO: ELIMINAR ESTE MÉTODO DEL REPOSITORIO
  // Se reutiliza el último StockLot creado en las últimas 24h
  // para consolidar devoluciones
//   Optional<StockLot> findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
//       Long productId,
//       LocalDateTime limitCreatedAt);

  // Query para la busqueda de lotes de stock para usuarios con el rol de
  // SECRETARY
  // Devuelve una pagina de lotes de stock que cumplen con los criterios de
  // busqueda
  @Query("""
      SELECT sl
      FROM StockLot sl
      JOIN sl.product p
      JOIN p.model m
      WHERE (:minQuantityReceived IS NULL OR sl.quantityReceived >= :minQuantityReceived)
      AND (:maxQuantityReceived IS NULL OR sl.quantityReceived <= :maxQuantityReceived)
      AND (:minQuantityAvailable IS NULL OR sl.maxQuantityAvailable >= :minQuantityAvailable)
      AND (:maxQuantityAvailable IS NULL OR sl.maxQuantityAvailable <= :maxQuantityAvailable)
      AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
      AND (
        :keyword IS NULL OR
        LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      AND (:companyId IS NULL OR sl.company.id = :companyId)
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND (:typeId IS NULL OR p.type.id = :typeId)

      """)
  Page<StockLot> findAllByParams(
      @Param("minQuantityReceived") Integer minQuantityReceived,
      @Param("maxQuantityReceived") Integer maxQuantityReceived,
      @Param("minQuantityAvailable") Integer minQuantityAvailable,
      @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("keyword") String keyword,
      @Param("companyId") Long companyId,
      @Param("categoryId") Long categoryId,
      @Param("typeId") Long typeId,
      Pageable pageable);

      // TODO: ELIMINAR ESTOS 3 MÉTODOS DEL REPOSITORIO, SE REUTILIZA EL MÉTODO findAllByParams
//   @Query("""
//       SELECT sl
//       FROM StockLot sl
//       WHERE
//       (:minQuantityReceived IS NULL OR sl.quantityReceived >= :minQuantityReceived)
//       AND (:maxQuantityReceived IS NULL OR sl.quantityReceived <= :maxQuantityReceived)
//       AND (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
//       AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
//       AND (:minQuantityDelivered IS NULL OR sl.quantityDelivered >= :minQuantityDelivered)
//       AND (:maxQuantityDelivered IS NULL OR sl.quantityDelivered <= :maxQuantityDelivered)
//       AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
//       AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
//       AND (:zeroStock IS NULL OR sl.zeroStock = :zeroStock)
//       AND (:companyId IS NULL OR sl.company.id = :companyId)
//       AND (sl.product.id = :productId)
//       """)
//   Page<StockLot> findAllByParamsAndProductId(
//       @Param("minQuantityAvailable") Integer minQuantityAvailable,
//       @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
//       @Param("minQuantityReceived") Integer minQuantityReceived,
//       @Param("maxQuantityReceived") Integer maxQuantityReceived,
//       @Param("minQuantityDelivered") Integer minQuantityDelivered,
//       @Param("maxQuantityDelivered") Integer maxQuantityDelivered,
//       @Param("minCreatedAt") LocalDateTime minCreatedAt,
//       @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
//       @Param("zeroStock") Boolean zeroStock,
//       @Param("companyId") Long companyId,
//       @Param("productId") Long productId,
//       Pageable pageable);

  // Query para la busqueda de lotes de stock para usuarios con el rol de OPERATOR
  // Devuelve una pagina de lotes de stock que cumplen con los criterios de
  // busqueda
//   @Query("""
//       SELECT sl
//       FROM StockLot sl
//       WHERE (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
//       AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
//       AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
//       AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
//       AND (:productName IS NULL OR LOWER(sl.product.name) LIKE LOWER(CONCAT('%', :productName, '%')))
//       AND (sl.zeroStock = false)
//       AND (:companyId IS NULL OR sl.company.id = :companyId)
//       """)
//   Page<StockLot> findAllByNotZeroStock(
//       @Param("minQuantityAvailable") Integer minQuantityAvailable,
//       @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
//       @Param("minCreatedAt") LocalDateTime minCreatedAt,
//       @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
//       @Param("productName") String productName,
//       @Param("companyId") Long companyId,
//       Pageable pageable);

  // Query para la busqueda de lotes de stock para usuarios con el rol de OPERATOR
  // Devuelve una pagina de lotes de stock que cumplen con los criterios de
  // busqueda
//   @Query("""
//         SELECT sl
//         FROM StockLot sl
//         WHERE (:minQuantityAvailable IS NULL OR sl.quantityAvailable >= :minQuantityAvailable)
//         AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <= :maxQuantityAvailable)
//         AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
//         AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
//         AND (sl.zeroStock = false)
//         AND (sl.product.id = :productId)
//         AND (:companyId IS NULL OR sl.company.id = :companyId)
//       """)
//   Page<StockLot> findAllByProductIdAndNotZeroStock(
//       @Param("minQuantityAvailable") Integer minQuantityAvailable,
//       @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
//       @Param("minCreatedAt") LocalDateTime minCreatedAt,
//       @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
//       @Param("productId") Long productId,
//       @Param("companyId") Long companyId,
//       Pageable pageable);



  // METODO EN EL REPOSITORIO PARA LISTAR TODOS LOS LOTES DE STOCK QUE PERTENEZCAN
  // AL MISMO MODELO, PERO CON LA CONDICION DE QUE PIDA COMO REQUISITO UN ID DE UN
  // LOTE DE STOCK PARA QUE SEA OMITIDO DE LA LISTA, ADEMÁS LOS LOTES DE STOCK NO DEBEN TENER LA CANTIDAD DISPONIBLE EN 0
  @Query("""
      SELECT sl
      FROM StockLot sl
      WHERE sl.model.id = :modelId
      AND sl.company.id = :companyId
      AND sl.zeroStock = false 
      AND sl.id != :stockLotId
      """)
  List<StockLot> findAllByModelIdAndCompanyIdAndExcludeOneStockLotByIdAndZeroStockIsFalse(Long modelId, Long companyId, Long stockLotId);

  // Obtiene la sumatoria de todos los campos quantityReceived de un producto por
  // su id
  @Query("""
          SELECT COALESCE(SUM(sl.quantityReceived), 0)
          FROM StockLot sl
          WHERE sl.model.id = :modelId
      """)
  Integer sumQuantityReceivedByModelId(@Param("modelId") Long modelId);

  // Obtiene la sumatoria de todos los campos quantityAvailable de un producto por
  // su id
  @Query("""
          SELECT COALESCE(SUM(sl.quantityAvailable), 0)
          FROM StockLot sl
          WHERE sl.model.id = :modelId
      """)
  Integer sumQuantityAvailableByModelId(@Param("modelId") Long modelId);

  @Query("""
              SELECT COALESCE(SUM(sl.quantityDelivered), 0)
              FROM StockLot sl
              WHERE sl.model.id = :modelId
      """)
  Integer sumQuantityDeliveredByModelId(@Param("modelId") Long modelId);

  // Query para obtener un lote de stock existente de un producto cuya fecha de
  // creación no sea mayor a 24 horas, para consolidar devoluciones, y que tenga
  // cantidad disponible
  @Query("""
          SELECT s
          FROM StockLot s
          WHERE s.company.id = :companyId
            AND s.model.id = :modelId
            AND s.temporary = true
            AND s.zeroStock = false
            AND s.createdAt >= :limitDate
      """)
  Optional<StockLot> findActiveTemporaryStockLot(
      @Param("companyId") Long companyId,
      @Param("modelId") Long modelId,
      @Param("limitDate") LocalDateTime limitDate);
}
