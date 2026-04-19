package com.pe.inventoryapp.backend.movement.model.dto;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementDto {
  private Long id;
  private Integer quantity;
  private MovementType movementType;
  private String userFirstname;
  private String userLastName;
  private Long modelId;
  private String modelName;
  private Long productId;
  private String productName;
}
