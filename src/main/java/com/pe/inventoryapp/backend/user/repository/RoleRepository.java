package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.user.model.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
  Optional<Role> findByName(String name);

  boolean existsByName(String name);
}
