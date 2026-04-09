package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class StockLotDomainService {

  // Genera el codigo del lote de entrega
  public String resolveBatch(String productName, String modelName, String companyName) {
    // // Obtiene la fecha de hoy por partes
    // LocalDateTime now = LocalDateTime.now();
    // String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" +
    // now.getYear();
    // String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

    // // Genera automaticamente el batch
    // String batch = "LOT_" + companyName + "_" + productName.replace(" ", "-") +
    // '_' + modelName.replace(" ", "-") + "_" + date + "_" + time;
    // return batch;

    // Nueva sintaxis, solamente toma las 3 primeras letras de la empresa y las
    // fechas
    String prefix = companyName.substring(0, 3).toUpperCase();

    LocalDateTime now = LocalDateTime.now();
    String date = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
    String time = now.format(DateTimeFormatter.ofPattern("HHmmss"));

    return "LOT-" + prefix + "-" + date + "-" + time;

  }
}
