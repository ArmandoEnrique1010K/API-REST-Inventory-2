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
public class StockLotDetailsResponse {
  private Long id;
  private String batch;
  private Integer quantityReceived;
  private Integer quantityAvailable;
  private Integer quantityDelivered;
  private Integer quantityLost;
  private Integer quantityRecovered;
  private boolean temporary;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Long modelId;
  private String modelName;
  private String modelImageUrl;

  private Long companyId;
  private String companyName;

  private Long productId;
  private String productName;

  private Long typeId;
  private String typeName;

  private Long categoryId;
  private String categoryName;
}
