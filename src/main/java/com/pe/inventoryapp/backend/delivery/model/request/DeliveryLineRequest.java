package com.pe.inventoryapp.backend.delivery.model.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineRequest {
  @NotNull(message = "Especifique la cantidad")
  private Integer requiredQuantity;

  @NotNull(message = "Seleccione una ubicación")
  private Long idLocation;

  @NotNull(message = "Seleccione una orden de entrega")
  private Long idDeliveryOrder;
  
  @Nullable
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime limitDate;

  @NotNull(message = "Seleccione un producto")
  private Long idProduct;
}
