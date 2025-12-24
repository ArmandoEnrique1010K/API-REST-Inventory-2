package com.pe.inventoryapp.backend.delivery.model.response;

import java.time.LocalDate;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineListResponse {
  private Long id;
  private Integer requiredQuantity;
  private Integer deliveredQuantity;
  private Integer pendingQuantity;
  private LocalDate limitDate;
  private PreparationStatus preparationStatus;
  private String location;
  private String region;
}
