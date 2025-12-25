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
public class DeliveryOrderDetailsResponse {
  private Long id;
  private String batch;
  private LocalDateTime limitDate;
  private String createdByUser;
  private String updatedByUser;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String preparationStatus;
}
