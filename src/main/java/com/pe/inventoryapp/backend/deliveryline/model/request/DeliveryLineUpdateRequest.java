package com.pe.inventoryapp.backend.deliveryline.model.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
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
public class DeliveryLineUpdateRequest {
  @NotNull(message = "Especifique la cantidad")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer requiredQuantity;
  @Nullable
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @FutureOrPresent(message = "La fecha de entrega debe ser igual o posterior a la fecha actual")
  private LocalDateTime limitDate;

  private String comment;
}
