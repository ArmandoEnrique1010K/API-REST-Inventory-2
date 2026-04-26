package com.pe.inventoryapp.backend.product.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
  private String name;

  @NotNull(message = "Introduzca una fecha")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @PastOrPresent(message = "La fecha de entrada no puede ser futura")
  private LocalDate entryDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Future(message = "La fecha de caducidad debe ser futura")
  private LocalDate caducityDate;

    // Cantidad minima
  @Nullable
  @PositiveOrZero(message = "Debe ser un número positivo")
  @Digits(integer = 10, fraction = 0, message = "Debe ser un número entero")
  private Integer minimumAvailableQuantity;
}
