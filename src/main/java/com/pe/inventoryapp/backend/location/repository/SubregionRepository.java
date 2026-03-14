package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.location.model.entity.Subregion;

public interface SubregionRepository extends JpaRepository<Subregion, Long> {
  @Query("SELECT s FROM Subregion s WHERE s.region.id = :regionId ORDER BY s.id DESC")
  List<Subregion> findAllByRegionId(@Param("regionId") Long regionId);

  Optional<Subregion> findByName(String name);

  boolean existsByNameAndRegionId(String name, Long regionId);
  boolean existsByNameAndRegionIdAndIdNot(String name, Long regionId, Long id);
}
