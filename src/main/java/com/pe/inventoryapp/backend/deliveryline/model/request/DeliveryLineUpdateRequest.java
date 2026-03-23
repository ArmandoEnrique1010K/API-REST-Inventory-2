package com.pe.inventoryapp.backend.deliveryline.model.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
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
public class DeliveryLineUpdateRequest {
  @NotNull(message = "Introduzca un valor númerico")
  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private Integer requiredQuantity;

  @Nullable
  @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @FutureOrPresent(message = "La fecha de entrega debe ser en el presente o en el futuro")
  private LocalDateTime limitDate;
  
  @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres")
  @Pattern(
    regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .,;:()\\-_/]*$",
    message = "El comentario contiene caracteres no permitidos"
  )
  private String movementComment;
}
