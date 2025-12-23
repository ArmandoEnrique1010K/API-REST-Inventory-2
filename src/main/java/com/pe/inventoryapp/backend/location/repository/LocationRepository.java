package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.location.model.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

  List<Location> findAllByStatusTrue();

  @Query("""
      SELECT l
      FROM Location l
      WHERE (:name IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:regionId IS NULL OR l.region.id = :regionId)
      AND (:status IS NULL OR l.status = :status)
      """)
  Page<Location> findAllByParams(
      @Param("name") String name,
      @Param("regionId") Long regionId,
      @Param("status") Boolean status,
      Pageable pageable);

  Optional<Location> findByName(String name);

  // List<Location> findByRegionId(Long regionId);
}
