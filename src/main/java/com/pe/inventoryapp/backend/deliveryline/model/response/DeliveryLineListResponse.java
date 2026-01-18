package com.pe.inventoryapp.backend.deliveryline.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;

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
  private LineStatus lineStatus;

  // TODO: ESTE CAMPO ES TEMPORAL
  private Long id_product;
  private String location;
  private String region;
}
