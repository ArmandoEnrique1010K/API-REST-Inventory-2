package com.pe.inventoryapp.backend.product.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.micrometer.common.lang.Nullable;
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
public class ProductCreateRequest {
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

  // Se necesita crear el primer modelo que se asociara al producto
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
  private String modelName;

  // URL absoluta de la imagen
  @NotBlank
  @Size(min = 4, max = 500, message = "La URL debe tener entre 4 y 500 caracteres")
  private String modelImageUrl;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @PastOrPresent(message = "La fecha de entrada no puede ser futura")
  @Nullable
  private LocalDate modelEntryDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Future(message = "La fecha de caducidad debe ser futura")
  private LocalDate modelCaducityDate;

  @NotNull(message = "Seleccione una categoria")
  private Long categoryId;

  @NotNull(message = "Seleccione un tipo")
  private Long typeId;
}
