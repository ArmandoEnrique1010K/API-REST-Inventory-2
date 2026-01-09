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
public class DeliveryOrderClientListResponse {
  private Long id;
  private String batch;
  private LocalDateTime limitDate;
  private LocalDateTime priorityDate;
  private OrderStatus status;
}
