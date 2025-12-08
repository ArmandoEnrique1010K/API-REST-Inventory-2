package com.pe.inventoryapp.backend.logistics.repository;

import com.pe.inventoryapp.backend.logistics.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
