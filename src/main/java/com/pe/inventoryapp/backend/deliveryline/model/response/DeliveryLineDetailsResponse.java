package com.pe.inventoryapp.backend.deliveryline.model.response;

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
public class DeliveryLineDetailsResponse {
  private Long id;
  private Integer requiredQuantity;
  private Integer deliveredQuantity;
  private Integer pendingQuantity;
  private LocalDateTime updatedAt;
  private LocalDateTime limitDate;
  private String updatedByUser;
  private PreparationStatus preparationStatus;
  private String location;
  private String region;
  private Long productId;
  private String productName;
  private String productImageUrl;
}
