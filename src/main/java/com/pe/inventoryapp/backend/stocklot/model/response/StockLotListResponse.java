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
public class StockLotListResponse {
  private Long id;
  private String batch;
  private Integer quantityAvailable;
  private Integer quantityReceived;
  private LocalDateTime createdAt;

  private Long modelId;
  private String modelName;
  private String modelImageUrl;

  private Long productId;
  private String productName;
}
