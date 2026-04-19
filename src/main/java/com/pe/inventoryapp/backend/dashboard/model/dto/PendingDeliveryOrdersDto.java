package com.pe.inventoryapp.backend.dashboard.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingDeliveryOrdersDto {
  private Long id;
  private String batch;
  private LocalDateTime priorityDate;
  private Double percentage;
}
