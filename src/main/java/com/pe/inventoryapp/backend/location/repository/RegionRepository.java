package com.pe.inventoryapp.backend.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.location.model.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
  Optional<Region> findByName(String name);

  boolean existsByName(String name);
  
  boolean existsByNameAndIdNot(String name, Long id);
}
