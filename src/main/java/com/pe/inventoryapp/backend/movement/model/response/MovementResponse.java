package com.pe.inventoryapp.backend.movement.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponse {
  private Long id;
  private LocalDateTime createdAt;
  private String username_snapshot;

}
