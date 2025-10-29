package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;

import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;

public interface UserService {

  List<ListUsersResponse> findAll();

  Optional<DetailUserResponse> findById(Long id);

  Optional<DetailUserResponse> findByEmail(String email);
  // Boolean getUserByEmail(String email);

  String remove(Long id);

  void verifyUser(String name);

}
