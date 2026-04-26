package com.pe.inventoryapp.backend.product.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDetailsResponse {
  private Long id;
  private String name;
  private String imageUrl;
  private LocalDate entryDate;
  private LocalDate caducityDate;
  private Integer totalQuantityAvailable;
  private Integer totalQuantityReceived;
  private Integer totalQuantityDelivered;
  private boolean status;
  private Integer minimumAvailableQuantity;
  private boolean lowStock;

  private Long productId;
  private String productName;
  private BigDecimal productLength;
  private BigDecimal productWidth;
  private BigDecimal productHeight;
  private boolean productStatus;

  private Long categoryId;
  private String categoryName;

  private Long typeId;
  private String typeName;
}
