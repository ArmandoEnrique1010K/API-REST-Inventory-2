package com.pe.inventoryapp.backend.movement.model.request;

import jakarta.validation.constraints.NotBlank;
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
public class MovementSendRequest {
  @NotNull
  private Integer quantity;
  private String comment;
  @NotBlank(message = "Introduzca un lote de entrega")
  private String batch;
  @NotNull
  private Long idProduct;
  @NotNull
  private Long idCompany;
}
