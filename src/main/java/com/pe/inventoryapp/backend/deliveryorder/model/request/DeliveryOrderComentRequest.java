package com.pe.inventoryapp.backend.deliveryorder.model.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderComentRequest {
  @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres")
  @Pattern(
    regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .,;:()\\-_/]*$",
    message = "El comentario contiene caracteres no permitidos"
  )
  private String movementComment;
}
