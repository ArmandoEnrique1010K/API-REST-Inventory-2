package com.pe.inventoryapp.backend.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.organization.model.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
