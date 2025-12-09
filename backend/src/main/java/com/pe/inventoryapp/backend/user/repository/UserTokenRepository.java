package com.pe.inventoryapp.backend.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.pe.inventoryapp.backend.user.model.entity.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
  UserToken findByValue(String value);

}
