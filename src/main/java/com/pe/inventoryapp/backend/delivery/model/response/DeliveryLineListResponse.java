package com.pe.inventoryapp.backend.delivery.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;

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
  private LocalDateTime limitDate;
  private PreparationStatus preparationStatus;
  private String location;
  private String region;
}
