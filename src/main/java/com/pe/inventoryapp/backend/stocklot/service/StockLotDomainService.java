package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class StockLotDomainService {
  
  public String resolveBatch(String productName, String modelName) {
    // Obtiene la fecha de hoy por partes
    LocalDateTime now = LocalDateTime.now();
    String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
    String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

    // Genera automaticamente el batch
    String batch = "LOT-" + productName.replace(" ", "-") + modelName.replace(" ", "-") + "-" + date + "-" + time;

    return batch;
  }
}
