package com.pe.inventoryapp.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase representa una respuesta que contiene datos, toda respuesta que devuelve datos es exitosa a pesar de no devolver ningun dato, es decir, un arreglo vacio
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 
public class DataResponse {
  private String type;
  private Integer status;
  private Object data;
}
