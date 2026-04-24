package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.location.model.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
  Optional<Region> findByName(String name);

  @Query("SELECT r FROM Region r ORDER BY r.id DESC")
  List<Region> findAllAndSortById();

  boolean existsByName(String name);
  boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
      SELECT DISTINCT r
      FROM DeliveryLine dl
      JOIN dl.location l
      JOIN l.subregion s
      JOIN s.region r
      WHERE dl.deliveryOrder.id = :deliveryOrderId
      ORDER BY r.name
      """)
  List<Region> findRegionsByDeliveryOrderId(Long deliveryOrderId);

}
