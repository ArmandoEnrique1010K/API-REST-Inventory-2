package com.pe.inventoryapp.backend.stocklot.model.response;

import java.time.LocalDate;
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
  private Integer deliveredTotal;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Long productId;
  private String productName;
  private String productImageUrl;

  private Long companyId;
  private String companyName;
}
