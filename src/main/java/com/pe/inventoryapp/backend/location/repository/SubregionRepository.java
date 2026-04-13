package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.location.model.entity.Subregion;

public interface SubregionRepository extends JpaRepository<Subregion, Long> {
  @Query("SELECT s FROM Subregion s JOIN FETCH s.region r WHERE r.id = :regionId ORDER BY s.id DESC")
  List<Subregion> findAllByRegionId(@Param("regionId") Long regionId);

  Optional<Subregion> findByName(String name);

  //* FETCH solo se usa cuando quieres traer entidades completas, no cuando haces
  // agregaciones como COUNT.
  @Query("SELECT COUNT(s) > 0 FROM Subregion s JOIN s.region r WHERE s.name = :name AND r.id = :regionId")
  boolean existsByNameAndRegionId(String name, Long regionId);

  @Query("SELECT COUNT(s) > 0 FROM Subregion s JOIN s.region r WHERE s.name = :name AND r.id = :regionId AND s.id != :id")
  boolean existsByNameAndRegionIdAndIdNot(String name, Long regionId, Long id);

  @Query("""
      SELECT s FROM Subregion s
      JOIN FETCH s.region r
      WHERE s.id = :id
      """)
  Optional<Subregion> findByIdFull(Long id);

}
