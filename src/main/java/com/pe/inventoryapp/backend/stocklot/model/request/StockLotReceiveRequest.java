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
  @NotNull(message = "Introduzca un valor númerico")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer quantity;

  // Opcionalmente puede introducir un comentario
  private String comment;

  // TODO: CREAR UN ENDPOINT PARA QUE DE ALGUNA MANERA DEBE LISTAR LOS MODELOS RELACIONADOS HACIA UN PRODUCTO POR ID
  @NotNull(message = "Seleccione un modelo del producto")
  private Long idModel;

  @NotNull(message = "Seleccione una empresa")
  private Long idCompany;
}
