package com.pe.inventoryapp.backend.product.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
public class ProductCreateRequest {
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
  private String name;

  // Las medidas deben ser un numero positivo o cero, con hasta 3 dígitos enteros y 2 decimales como maximo
  // @NotNull(message = "Introduzca un valor númerico")
  @Nullable
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal length;

  // @NotNull(message = "Introduzca un valor númerico")
  @Nullable
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal width;

  // @NotNull(message = "Introduzca un valor númerico")
  @Nullable
  @PositiveOrZero(message = "La medida debe ser un valor positivo")
  @Digits(integer = 3, fraction = 2, message = "La longitud debe tener hasta 3 dígitos enteros y 2 decimales")
  private BigDecimal height;


  // RELACIONADO AL MODELO
  
  // Se necesita crear el primer modelo que se asociara al producto
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
  private String modelName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @PastOrPresent(message = "La fecha de entrada no puede ser futura")
  // @Nullable // permite valor null
  @NotNull(message = "Introduzca una fecha")
  private LocalDate modelEntryDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Future(message = "La fecha de caducidad debe ser futura")
  private LocalDate modelCaducityDate;

  // Cantidad minima
  @Nullable
  @Positive(message = "Debe ser un número positivo")
  @Digits(integer = 10, fraction = 0, message = "Debe ser un número entero")
  private Integer modelMinimumAvailableQuantity;

  @NotNull(message = "Seleccione una categoria")
  private Long categoryId;

  @NotNull(message = "Seleccione un tipo")
  private Long typeId;
}
