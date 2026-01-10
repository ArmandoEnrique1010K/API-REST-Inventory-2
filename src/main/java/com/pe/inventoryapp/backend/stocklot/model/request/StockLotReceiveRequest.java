package com.pe.inventoryapp.backend.stocklot.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLotReceiveRequest {
  @NotNull
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer quantity;

  // Opcionalmente puede introducir un comentario
  private String comment;

  @NotNull(message = "Seleccione un producto")
  private Long idProduct;

  @NotNull(message = "Seleccione una empresa")
  private Long idCompany;
}
