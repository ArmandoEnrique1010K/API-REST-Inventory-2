package com.pe.inventoryapp.backend.deliveryorder.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OnTimeStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;

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
  private LocalDateTime priorityDate;
  private String createdByUser;
  private String updatedByUser;
  private String userClientFullname;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private OrderStatus orderStatus;
  
  private Double percentage;
  private LocalDateTime deliveredAt;
  private OnTimeStatus onTimeStatus;
}
