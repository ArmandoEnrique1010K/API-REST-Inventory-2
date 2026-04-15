package com.pe.inventoryapp.backend.user.model.mapper;


import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersByRoleUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.model.response.RolesByUserResponse;

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

  public ListUsersResponse buildListUserResponse() {

    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    // Verifica si el usuario tiene los roles de MANAGER o ADMIN
    // boolean isManager = user.getRoles().stream().anyMatch(r ->
    // "ROLE_MANAGER".equals(r.getName()));
    // boolean isAdmin = user.getRoles().stream().anyMatch(r ->
    // "ROLE_ADMIN".equals(r.getName()));

    // List<String> roles = user.getRoles().stream()
    //     .map(role -> role.getLabel())
    //     .toList().reversed();

    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new ListUsersResponse(
        user.getId(),
        user.getFirstname()
            .trim(),
        user.getLastname()
            .trim(),
        user.getDni(),
        user.getRole(),
        user.isActive());
  }

  public DetailUserResponse buildDetailUserResponse() {

    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    // List<String> roles = user.getRoles().stream()
    //     .map(role -> role.getLabel())
    //     .toList().reversed();

    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new DetailUserResponse(
        user.getFirstname()
            .trim(),
        user.getLastname()
            .trim(),
        user.getEmail()
            .trim(),
        user.getDni(),
        user.getRole() );

  }

  public ProfileRequest buildProfileRequest() {
    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    return new ProfileRequest(
        user.getFirstname(),
        user.getLastname(),
        user.getEmail(),
        user.getDni());
  }

  public ListUsersByRoleUserResponse buildListUsersByRoleUserResponse() {
    if (user == null) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    return new ListUsersByRoleUserResponse(
        user.getId(),
        user.getFirstname() + " " + user.getLastname(),
        user.getEmail(),
        user.getDni()
    );
  }

  public RolesByUserResponse buildRolesByUserResponse() {
    if (user == null ) {
      throw new RuntimeException("Debe pasar la entidad User");
    }

    return new RolesByUserResponse(
        // user.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getName())),
        // user.getRoles().stream().anyMatch(role -> "ROLE_OPERATOR".equals(role.getName())), 
        // user.getRoles().stream().anyMatch(role -> "ROLE_SECRETARY".equals(role.getName())),
        // user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))
        user.getRole().toString()
      );
  }

}
