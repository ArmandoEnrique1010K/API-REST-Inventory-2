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
public class StockLotTransferRequest {
  @NotNull
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer quantity;
  private String comment;

  // IDs de los lotes de stocks de emisor y receptor
  // @NotNull
  // private Long idStockLotEmitter;

  @NotNull
  private Long idStockLotReceiver;

}
