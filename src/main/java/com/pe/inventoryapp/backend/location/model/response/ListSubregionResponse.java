package com.pe.inventoryapp.backend.location.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListSubregionResponse {
  private Long id;
  private String name;
}
