package com.pe.inventoryapp.backend.organization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.organization.model.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

  Optional<Region> findByName(String name);

}
