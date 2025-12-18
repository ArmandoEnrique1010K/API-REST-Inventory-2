package com.pe.inventoryapp.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// Esta clase representa una respuesta común, que se puede utilizar tanto para
// respuestas exitosas como para respuestas erroneas
public class CommonResponse {
  private String type;
  private String code;
  private String message;
}
