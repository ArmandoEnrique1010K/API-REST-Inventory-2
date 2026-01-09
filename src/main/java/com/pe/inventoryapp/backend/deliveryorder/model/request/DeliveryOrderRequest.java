package com.pe.inventoryapp.backend.deliveryorder.model.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderRequest {
  private Long idClient;
  @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime limitDate;
}
