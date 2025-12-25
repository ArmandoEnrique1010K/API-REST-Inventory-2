package com.pe.inventoryapp.backend.delivery.model.request;

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
public class DeliveryLineUpdateRequest {
  @NotNull(message = "Especifique la cantidad")
  private Integer requiredQuantity;
  @Nullable
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime limitDate;
}
