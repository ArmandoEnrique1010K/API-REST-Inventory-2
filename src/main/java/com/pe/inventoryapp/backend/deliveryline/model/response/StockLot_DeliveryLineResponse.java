package com.pe.inventoryapp.backend.deliveryline.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
// Historial de cantidades tomadas de distintos lotes de entrega
public class StockLot_DeliveryLineResponse {
  private Long id;
  private Integer quantityUsed;
  private LocalDateTime createdAt;

  private Long stockLotId;
  private String stockLotBatch;
}
