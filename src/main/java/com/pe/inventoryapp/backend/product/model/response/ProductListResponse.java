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
  private String imageUrl;
  private Integer totalQuantityAvailable;
  private boolean status;
  private String categoryName;
}
