package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.user.model.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findUserByEmail(String email);

    Boolean existsByEmail(String email);
}