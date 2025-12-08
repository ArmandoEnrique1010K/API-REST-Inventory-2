package com.pe.inventoryapp.backend.logistics.repository;

import com.pe.inventoryapp.backend.logistics.model.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
