package com.pe.inventoryapp.backend.user.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailUserResponse {
  private String firstname;
  private String lastname;
  private String email;
  private Integer dni;
  private List<String> roles;
  private boolean status;
}
