package com.pe.inventoryapp.backend.product.model.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductUpdateRequest {
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
  private String name;

  // Las medidas deben ser un numero positivo o cero, con hasta 3 dígitos enteros y 2 decimales como maximo
  @NotNull
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal length;

  @NotNull
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal width;

  @NotNull
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal height;

  @NotNull(message = "Seleccione una categoria")
  private Long categoryId;

  @NotNull(message = "Seleccione un tipo")
  private Long typeId;
}
