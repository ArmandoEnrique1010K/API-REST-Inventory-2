package com.pe.inventoryapp.backend.stocklot.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLotAdjustmentRequest {
  @NotNull(message = "Introduzca un valor númerico")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer quantity;

  @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres")
  @Pattern(
    regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .,;:()\\-_/]*$",
    message = "El comentario contiene caracteres no permitidos"
  )
  private String comment;
}
