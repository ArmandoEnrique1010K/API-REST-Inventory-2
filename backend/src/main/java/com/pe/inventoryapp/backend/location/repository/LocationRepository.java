package com.pe.inventoryapp.backend.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.location.model.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

  List<Location> findAllByStatusTrue();

  Optional<Location> findByName(String name);

  List<Location> findByRegionId(Long regionId);
}
