package com.pe.inventoryapp.backend.movement.model.response;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementsTodaySummaryResponse {
  private Long id;
  private Integer quantity;
  private MovementType movementType;
  private String userFirstname;
  private String userLastname;
  private Long modelId;
  private String modelName;
  private Long productId;
  private String productName;
}
