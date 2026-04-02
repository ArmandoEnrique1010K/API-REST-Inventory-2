package com.pe.inventoryapp.backend.product.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelListSearchResponse {
  private Long id;
  private String name;
  private String imageUrl;
  private String productName;
  private String typeName;
  private Long categoryId;
  private String categoryName;
}
