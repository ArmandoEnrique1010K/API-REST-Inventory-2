package com.pe.inventoryapp.backend.stock.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
  @NotBlank(message = "Introduzca la orden de entrega")
  private String batch;
  @NotNull(message = "Seleccione la cantidad recibida")
  private Integer quantityReceived;
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Nullable
  private LocalDate caducityDate;
}
