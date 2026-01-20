package com.pe.inventoryapp.backend.deliveryline.model.mapper;

import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.response.StockLot_DeliveryLineResponse;

public class StockLot_DeliveryLineMapper {
    private StockLot_DeliveryLine stockLot_DeliveryLine;

  private StockLot_DeliveryLineMapper() {
  }

  public static StockLot_DeliveryLineMapper builder() {
    return new StockLot_DeliveryLineMapper();
  }

  public StockLot_DeliveryLineMapper setStockLot_DeliveryLine(StockLot_DeliveryLine stockLot_DeliveryLine) {
    this.stockLot_DeliveryLine = stockLot_DeliveryLine;
    return this;
  }


  public StockLot_DeliveryLineResponse buildStockLot_DeliveryLineResponse() {
    if (stockLot_DeliveryLine == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryLine");
    } else {
      return new StockLot_DeliveryLineResponse(
          stockLot_DeliveryLine.getId(),
          stockLot_DeliveryLine.getQuantityUsed());
    }
  }



}
