package com.pe.inventoryapp.backend.product.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
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

  @NotBlank(message = "Suba una imagen")
  @Size(min = 4, max = 500, message = "La URL debe tener entre 4 y 500 caracteres")
  private String imageUrl;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @PastOrPresent(message = "La fecha de entrada no puede ser futura")
  private LocalDate entryDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Future(message = "La fecha de caducidad debe ser futura")
  private LocalDate caducityDate;
}
