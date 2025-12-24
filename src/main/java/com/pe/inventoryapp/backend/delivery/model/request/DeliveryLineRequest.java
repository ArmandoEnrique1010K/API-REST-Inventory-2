package com.pe.inventoryapp.backend.delivery.model.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineRequest {

  // TODO: SE PODRIA ELIMINAR ESTE REQUEST
  private Integer requiredQuantity;
  private Long idLocation;
  private Long idDeliveryOrder;
  private LocalDate limitDate;

  // TODO: Este campo puede ser opcional
  private Long idStockLot;
}
