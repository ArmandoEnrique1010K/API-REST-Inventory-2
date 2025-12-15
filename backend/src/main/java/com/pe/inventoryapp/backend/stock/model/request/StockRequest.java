package com.pe.inventoryapp.backend.stock.model.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
  private String batch;
  private Integer quantityReceived;
  private LocalDate caducityDate;
}
