package com.pe.inventoryapp.backend.stocklot.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

import jakarta.persistence.LockModeType;

public interface StockLotRepository extends JpaRepository<StockLot, Long>, JpaSpecificationExecutor<StockLot> {

      // Query para la busqueda de lotes de stock para usuarios con el rol de
      // SECRETARY
      // Devuelve una pagina de lotes de stock que cumplen con los criterios de
      // busqueda
      // @Query("""
      // SELECT sl
      // FROM StockLot sl
      // JOIN sl.model m
      // JOIN m.product p
      // WHERE (:minQuantityReceived IS NULL OR sl.quantityReceived >=
      // :minQuantityReceived)
      // AND (:maxQuantityReceived IS NULL OR sl.quantityReceived <=
      // :maxQuantityReceived)
      // AND (:minQuantityAvailable IS NULL OR sl.quantityAvailable >=
      // :minQuantityAvailable)
      // AND (:maxQuantityAvailable IS NULL OR sl.quantityAvailable <=
      // :maxQuantityAvailable)
      // AND (:minCreatedAt IS NULL OR sl.createdAt >= :minCreatedAt)
      // AND (:maxCreatedAt IS NULL OR sl.createdAt <= :maxCreatedAt)
      // AND (
      // :keyword IS NULL OR
      // LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
      // LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      // )
      // AND (:companyId IS NULL OR sl.company.id = :companyId)
      // AND (:categoryId IS NULL OR p.category.id = :categoryId)
      // AND (:typeId IS NULL OR p.type.id = :typeId)
      // AND (:modelId IS NULL OR m.id = :modelId)
      // AND sl.zeroStock = false
      // ORDER BY sl.createdAt DESC
      // """)
      // Page<StockLot> findAllByParams(
      // @Param("minQuantityReceived") Integer minQuantityReceived,
      // @Param("maxQuantityReceived") Integer maxQuantityReceived,
      // @Param("minQuantityAvailable") Integer minQuantityAvailable,
      // @Param("maxQuantityAvailable") Integer maxQuantityAvailable,
      // @Param("minCreatedAt") LocalDateTime minCreatedAt,
      // @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      // @Param("keyword") String keyword,
      // @Param("companyId") Long companyId,
      // @Param("categoryId") Long categoryId,
      // @Param("typeId") Long typeId,
      // @Param("modelId") Long modelId,
      // Pageable pageable);

      // METODO PARA OBTENER UNA LISTA DE LOS LOTES DE ENTREGA QUE PERTENECEN A UN
      // MISMO MODELO Y QUE ESTEN EN ZEROSTOCK EN FALSE



      /**
       * Se usa JOIN FETCH para evitar el problema N+1 en relaciones anidadas.
       *
       * IMPORTANTE:
       * - Hibernate NO hace fetch recursivo automáticamente.
       * - Cada relación que se necesite debe ser explicitamente "fetch".
       *
       * Ejemplo:
       * StockLot → Model → Product → Category / Type
       *
       * Si no se hace JOIN FETCH en toda la cadena:
       * - Hibernate ejecutará queries adicionales por cada relación (N+1 problem)
       *
       * Este enfoque:
       * Trae todo en una sola query
       * Puede generar queries más pesadas (más JOINs)
       *
       * Alternativa:
       * - Usar @BatchSize para carga por lotes en lugar de un JOIN masivo
       */

      @Query("""
                  SELECT sl
                  FROM StockLot sl
                      JOIN FETCH sl.model m
                      JOIN FETCH m.product p
                      JOIN FETCH p.category
                      JOIN FETCH p.type
                      JOIN FETCH sl.company c
                  WHERE m.id = :modelId
                  AND sl.zeroStock = false
                  ORDER BY sl.createdAt DESC
                  """)
      List<StockLot> findAllActivesByModelId(
                  @Param("modelId") Long modelId);

      // METODO EN EL REPOSITORIO PARA LISTAR TODOS LOS LOTES DE STOCK QUE PERTENEZCAN
      // AL MISMO MODELO, PERO CON LA CONDICION DE QUE PIDA COMO REQUISITO UN ID DE UN
      // LOTE DE STOCK PARA QUE SEA OMITIDO DE LA LISTA, ADEMÁS LOS LOTES DE STOCK NO
      // DEBEN TENER LA CANTIDAD DISPONIBLE EN 0

      // * OBLIGATORIAMENTE SE DEBE ENCADENAR LAS DEMÁS ENTIDADES */
      @Query("""
                      SELECT sl
                      FROM StockLot sl
                      JOIN FETCH sl.model m
                      JOIN FETCH m.product p
                      JOIN FETCH p.category
                      JOIN FETCH p.type
                      JOIN FETCH sl.company c
                      WHERE m.id = :modelId
                      AND c.id = :companyId
                      AND sl.zeroStock = false
                      AND sl.id != :stockLotId
                  """)
      List<StockLot> findAllByModelIdAndCompanyIdAndExcludeOneStockLotByIdAndZeroStockIsFalse(
                  Long modelId, Long companyId, Long stockLotId);

      // Query para obtener un lote de stock existente de un producto cuya fecha de
      // creación no sea mayor a 24 horas, para consolidar devoluciones, y que tenga
      // cantidad disponible
      @Query("""
                      SELECT sl
                      FROM StockLot sl
                      JOIN FETCH sl.company c
                      JOIN FETCH sl.model m
                      WHERE c.id = :companyId
                        AND m.id = :modelId
                        AND sl.temporary = true
                        AND sl.zeroStock = false
                        AND sl.createdAt >= :limitDate
                  """)
      Optional<StockLot> findActiveTemporaryStockLot(
                  @Param("companyId") Long companyId,
                  @Param("modelId") Long modelId,
                  @Param("limitDate") LocalDateTime limitDate);



    // NUEVO METODO PARA BUSCAR UN LOTE DE STOCK POR ID
    @Query("""
            SELECT sl FROM StockLot sl
                      JOIN FETCH sl.model m
                      JOIN FETCH m.product p
                      JOIN FETCH p.category
                      JOIN FETCH p.type
                      JOIN FETCH sl.company c
                      WHERE sl.id = :id
            """)
    Optional<StockLot> findByIdFull(Long id);

    /**
     * Con findById:
     * 
     * model → puede cargarse (si es EAGER o se accede dentro de la transacción)
     * product → probablemente LAZY
     * category y type → NO se cargan
     */




      // @Lock(PESSIMISTIC_WRITE):
      // Bloquea las filas de la base de datos correspondientes a los StockLot
      // seleccionados
      // durante toda la transacción. Mientras estén bloqueadas, ningún otro proceso
      // puede
      // leerlas para escritura ni modificarlas hasta que la transacción termine
      // (commit o rollback).
      // Se usa para evitar inconsistencias en escenarios concurrentes, como la
      // asignación de stock.
      @Lock(LockModeType.PESSIMISTIC_WRITE)
      @Query("SELECT s FROM StockLot s WHERE s.id IN :ids")
      List<StockLot> findAllByIdForUpdate(@Param("ids") List<Long> ids);

      /*
       * IMPORTANTE:
       * Este método sobreescribe el findAll de JpaSpecificationExecutor
       * y permite usar Specifications + EntityGraph.
       *
       * PROBLEMA:
       * EntityGraph NO siempre funciona bien con relaciones anidadas cuando se usa
       * Specification.
       *
       * SOLUCIÓN:
       * Se delega el control real del fetch a Specification (fetchRelations),
       * ya que es más consistente y controlable.
       * 
       * ... ve a la clase StockLotSpecifications
       */

      @EntityGraph(attributePaths = {
                  "model",
                  "company",
                  "model.product",
                  "model.product.category",
                  "model.product.type"

      })
      @NonNull
      Page<StockLot> findAll(
                  @Nullable Specification<StockLot> spec,
                  @Nullable Pageable pageable);
}
