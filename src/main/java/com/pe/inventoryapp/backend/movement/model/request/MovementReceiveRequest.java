package com.pe.inventoryapp.backend.movement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para enviar stock al almacen
// Se produce cuando se quiere enviar stock al almacen directamente
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementReceiveRequest {
  @NotNull
  private Integer quantity;
  private String comment;
  @NotNull(message = "Seleccione un producto")
  private Long idProduct;
  @NotNull(message = "Seleccione una empresa")
  private Long idCompany;
}
