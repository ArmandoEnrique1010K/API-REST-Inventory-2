package com.pe.inventoryapp.backend.delivery.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderListResponse {
  private Long id;
  private String batch;
  private LocalDateTime limitDate;
  private String createdByUser;
  private PreparationStatus status;
}
