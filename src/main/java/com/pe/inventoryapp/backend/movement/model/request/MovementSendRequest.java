package com.pe.inventoryapp.backend.movement.model.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementSendRequest {
  private Integer quantity;
  private String comment;
  private String batch;
  private Long idProduct;
  private Long idCompany;
  private LocalDate caducityDate;
}
