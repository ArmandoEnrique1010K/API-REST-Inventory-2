package com.pe.inventoryapp.backend.product.model.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelListResponse {
  private Long id;
  private String name;
  private String imageUrl;
  private LocalDate entryDate;
  private LocalDate caducityDate;
  private Integer totalQuantityAvailable;
  private Integer totalQuantityReceived;
  private Integer totalQuantityDelivered;
  private boolean status;

  private Long productId;
  private String productName;
  private String typeName;
  private String categoryName;
}
