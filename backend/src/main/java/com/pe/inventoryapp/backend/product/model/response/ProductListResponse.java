package com.pe.inventoryapp.backend.product.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
  private Long id;
  private String name;
  private Integer stock;
  private String imageUrl;
  private String categoryName;
}
