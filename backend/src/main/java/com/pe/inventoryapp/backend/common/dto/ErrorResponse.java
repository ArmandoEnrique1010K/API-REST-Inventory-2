package com.pe.inventoryapp.backend.common.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ErrorResponse {
  private String type;
  private String message;
  private Map<String, String> errors;
}
