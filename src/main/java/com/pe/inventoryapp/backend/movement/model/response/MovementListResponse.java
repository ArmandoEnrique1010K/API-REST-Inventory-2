package com.pe.inventoryapp.backend.movement.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementListResponse {
  private Long id;
  private Integer quantity;
  private LocalDateTime createdAt;
  private MovementType movementType;
  private String userName;

  private Long modelId;
  private String modelName;
}
