package com.pe.inventoryapp.backend.product.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelSearchResponse {
  private Long id;
  private String name;
  private String imageUrl;
  private String productName;
  private String typeName;
  private String categoryName;
}
