package com.pe.inventoryapp.backend.delivery.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DeliveryOrderResponse {
  private Long id;
  private String batch;
  private LocalDateTime deliveredDate;
  private String createdBy;
  private Integer quantityTotal;
  private String preparationStatus;
}
