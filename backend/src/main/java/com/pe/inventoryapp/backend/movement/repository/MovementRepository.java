package com.pe.inventoryapp.backend.movement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.movement.model.entity.Movement;

public interface MovementRepository extends JpaRepository<Movement, Long> {

}
