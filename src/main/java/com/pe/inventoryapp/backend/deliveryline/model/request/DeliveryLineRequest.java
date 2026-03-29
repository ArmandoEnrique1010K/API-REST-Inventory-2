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
public class DeliveryLineRequest {
  @NotNull(message = "Introduzca un valor númerico")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer requiredQuantity;

  @NotNull(message = "Seleccione una ubicación")
  private Long locationId;

  @NotNull(message = "Seleccione un modelo de producto")
  private Long modelId;

  @Nullable
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @FutureOrPresent(message = "La fecha de entrega debe ser en el presente o en el futuro")
  private LocalDateTime limitDate;



}
