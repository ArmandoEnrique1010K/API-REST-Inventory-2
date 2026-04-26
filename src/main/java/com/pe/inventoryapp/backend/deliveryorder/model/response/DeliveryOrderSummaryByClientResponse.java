package com.pe.inventoryapp.backend.deliveryorder.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderSummaryByClientResponse {
  private Long id;
  private String batch;
  private LocalDateTime priorityDate;
  private Double percentage;
  private OrderStatus orderStatus;
}
