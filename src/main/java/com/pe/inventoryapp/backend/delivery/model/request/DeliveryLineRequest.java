package com.pe.inventoryapp.backend.delivery.model.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineRequest {
  private Integer requiredQuantity;
  private Long idLocation;
  private Long idDeliveryOrder;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime limitDate;

  // TODO: Este campo puede ser opcional (SE PUEDE ELIMINAR)
  private Long idStockLot;
}
