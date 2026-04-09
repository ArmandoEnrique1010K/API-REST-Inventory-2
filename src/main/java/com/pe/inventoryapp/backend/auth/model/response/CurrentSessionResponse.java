package com.pe.inventoryapp.backend.auth.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CurrentSessionResponse {
  private String email;
  private List<String> roles;
}
