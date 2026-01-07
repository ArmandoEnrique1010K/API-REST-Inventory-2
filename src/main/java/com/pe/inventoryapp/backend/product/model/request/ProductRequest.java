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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
  @NotBlank(message = "Introduzca un nombre para el producto")
  private String name;

  // Estos campos son opcionales
  // Las medidas se expresan en centimetros
  @Digits(integer = 3, fraction = 2)
  @Positive
  @Nullable
  private BigDecimal length;

  @Digits(integer = 3, fraction = 2)
  @Positive
  @Nullable
  private BigDecimal width;

  // URL absoluta de la imagen
  private String imageUrl;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @PastOrPresent
  @Nullable
  private LocalDate entryDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Future
  @Nullable
  private LocalDate caducityDate;

  @NotNull(message = "Seleccione una categoria")
  private Long idCategory;
}
