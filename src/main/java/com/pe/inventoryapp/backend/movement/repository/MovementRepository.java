package com.pe.inventoryapp.backend.movement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;

public interface MovementRepository extends JpaRepository<Movement, Long> {

  // Query personalizado para buscar movimientos mediante parametros
  @Query("""
      SELECT mv
      FROM Movement mv
      JOIN mv.model m
      JOIN m.product p
      JOIN mv.user u
      LEFT JOIN mv.deliveryLine dl
      LEFT JOIN mv.stockLotReceiver s
      WHERE (:minQuantity IS NULL OR mv.quantity >= :minQuantity)
      AND (:maxQuantity IS NULL OR mv.quantity <= :maxQuantity)
      AND (:minCreatedAt IS NULL OR mv.createdAt >= :minCreatedAt)
      AND (:maxCreatedAt IS NULL OR mv.createdAt <= :maxCreatedAt)
      AND (:movementType IS NULL OR mv.movementType = :movementType)
      AND (
        :username IS NULL OR 
        LOWER(u.firstname) LIKE LOWER(CONCAT('%', :username, '%')) OR 
        LOWER(u.lastname) LIKE LOWER(CONCAT('%', :username, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT('%', :username, '%')) OR
        CAST(u.dni AS string) LIKE CONCAT('%', :username, '%'))
      AND (
        :keyword IS NULL OR
        LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      ORDER BY mv.createdAt DESC
  """)
  Page<Movement> findAllByParams(
      Pageable pageable,
      @Param("minQuantity") Integer minQuantity,
      @Param("maxQuantity") Integer maxQuantity,
      @Param("minCreatedAt") LocalDateTime minCreatedAt,
      @Param("maxCreatedAt") LocalDateTime maxCreatedAt,
      @Param("movementType") MovementType movementType,
      @Param("username") String username,
      @Param("keyword") String keyword
    );

    @Query("SELECT COUNT(m) FROM Movement m")
    long countAllMovements();

    // Devuelve el ID del movimiento más antiguo
    // Optional<Long> findFirstByOrderByCreatedAtAsc();

    Optional<Movement> findFirstByOrderByCreatedAtAsc();
    // * METODO PARA LIMITAR LA CANTIDAD DE MOVIMIENTOS EN LA BASE DE DATOS A 2000
    // MOVIMIENTOS
    @Query("SELECT m.id FROM Movement m ORDER BY m.createdAt ASC")
    List<Long> findOldestIds(Pageable pageable);
    // Pageable permite limitar la cantidad de resultados desde la base de datos
    // directamente (LIMIT equivalente en SQL), sin traer todos los registros y sin
    // crear listas manuales.

    // TODO: EXISTE LA POSIBILIDAD DE LLAMAR A UN METODO DEL REPOSITORIO DESPUES DE UNA OPERACION DEL MISMO REPOSITORIO???
}
