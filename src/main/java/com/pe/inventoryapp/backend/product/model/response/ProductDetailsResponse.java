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
public class ProductDetailsResponse {
  private Long id;
  private String name;
  private BigDecimal length;
  private BigDecimal width;
  private String imageUrl;
  private LocalDate entryDate;
  private LocalDate caducityDate;
  private Integer totalQuantityAvailable;
  private boolean status;
  private String categoryName;
}
