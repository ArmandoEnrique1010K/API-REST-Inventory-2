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
public class ProductDetailsResponse {
  private Long id;
  private String name;
  private LocalDate entryDate;
  private LocalDate caducityDate;

  private Double length;
  private Double width;
  private Double height;

  private Integer stock;

  private String imageUrl;

  private boolean status;
  private String categoryName;
}
