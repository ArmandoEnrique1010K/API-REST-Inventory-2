package com.pe.inventoryapp.backend.user.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListUsersResponse {
  private Long id;
  private String firstname;
  private String lastname;
  private Integer dni;
  private List<String> roles;

  // private boolean isManager;
  // private boolean isAdmin;
}
