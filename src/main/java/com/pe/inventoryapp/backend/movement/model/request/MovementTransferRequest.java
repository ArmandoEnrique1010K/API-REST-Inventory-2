package com.pe.inventoryapp.backend.movement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// TRANSFERIR STOCK ENTRE DOS LOTES DEL MISMO PRODUCTO
public class MovementTransferRequest {
  private Integer quantity;
  // private String comment;
  
  // IDs de los lotes de stocks de emisor y receptor
  @NotNull
  private Long idStockLotEmitter;
  @NotNull
  private Long idStockLotReceiver;
}
