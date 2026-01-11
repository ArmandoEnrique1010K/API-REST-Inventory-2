package com.pe.inventoryapp.backend.stocklot.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLotSameProductListResponse {
  private Long id;
  private String batch;
  private Integer quantityAvailable;
  private LocalDateTime createdAt;
}
