package com.pe.inventoryapp.backend.deliveryorder.model.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderRequest {
  @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @FutureOrPresent(message = "La fecha de entrega debe ser en el presente o en el futuro")
  private LocalDateTime limitDate;

  @NotNull
  private Long idClient;
}
