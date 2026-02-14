package com.pe.inventoryapp.backend.product.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private BigDecimal length;
  private BigDecimal width;
  private BigDecimal height;
  private Integer quantityModels;
  private boolean status;

  private Long categoryId;
  private String categoryName;

  private Long typeId;
  private String typeName;
}
