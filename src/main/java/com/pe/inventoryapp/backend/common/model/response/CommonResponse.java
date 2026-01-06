package com.pe.inventoryapp.backend.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase representa una respuesta común, que se puede utilizar tanto para
// respuestas exitosas como para respuestas erroneas
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
  private String type;
  private Integer status;
  private String message;
}
