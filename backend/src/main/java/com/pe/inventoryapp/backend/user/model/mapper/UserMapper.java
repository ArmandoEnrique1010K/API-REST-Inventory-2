package com.pe.inventoryapp.backend.user.model.mapper;

import com.pe.inventoryapp.backend.user.model.dto.UserRequest;
import com.pe.inventoryapp.backend.user.model.dto.UserResponse;
import com.pe.inventoryapp.backend.user.model.entity.User;

public class UserMapper {
  private User user;

  private UserMapper() {

  }

  public static UserMapper builder() {
    return new UserMapper();
  }

  public UserMapper setUser(User user) {
    this.user = user;
    return this;
  }

  public UserResponse buildUserResponse() {

    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    // Verifica si el usuario tiene los roles de MANAGER o ADMIN
    boolean isManager = user.getRoles().stream().anyMatch(r -> "ROLE_MANAGER".equals(r.getName()));
    boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new UserResponse(
        user.getId(),
        user.getFirstname(),
        user.getLastname(),
        user.getEmail(),
        isManager,
        isAdmin);
  }

  public UserRequest buildUserRequest() {

    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    // Verifica si el usuario tiene los roles de MANAGER o ADMIN
    boolean isManager = user.getRoles().stream().anyMatch(r -> "ROLE_MANAGER".equals(r.getName()));
    boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new UserRequest(
        user.getId(),
        user.getFirstname(),
        user.getLastname(),
        user.getEmail(),
        isManager,
        isAdmin);

  }

}
