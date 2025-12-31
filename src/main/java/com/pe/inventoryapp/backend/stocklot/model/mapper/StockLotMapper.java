package com.pe.inventoryapp.backend.stocklot.model.mapper;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;

public class StockLotMapper {
  private StockLot stockLot;

  private StockLotMapper() {

  }

  public static StockLotMapper builder() {
    return new StockLotMapper();
  }

  public StockLotMapper setStockLot(StockLot stockLot) {
    this.stockLot = stockLot;
    return this;
  }

  public StockLotListResponse buildStockLotListResponse() {
    if (stockLot == null) {
      throw new RuntimeException("Debe pasar la entidad Company");
    }

    return new StockLotListResponse(
      stockLot.getId(),
      stockLot.getBatch().trim(),
      stockLot.getQuantityAvailable(),
      stockLot.getCreatedAt(),
      stockLot.getProduct().getId(),
      stockLot.getProduct().getName(),
      stockLot.getProduct().getImageUrl());
  }

  public StockLotDetailsResponse buildStockLotDetailsResponse() {
    if (stockLot == null) {
      throw new RuntimeException("Debe pasar la entidad Company");
    } 
    
    return new StockLotDetailsResponse(
        stockLot.getId(),
        stockLot.getBatch().trim(),
        stockLot.getQuantityReceived(),
        stockLot.getQuantityAvailable(),
        stockLot.getDeliveredTotal(),
        stockLot.getCreatedAt(),
        stockLot.getUpdatedAt(),
        stockLot.getProduct().getId(),
        stockLot.getProduct().getName(),
        stockLot.getProduct().getImageUrl(),
        stockLot.getCompany().getId(),
        stockLot.getCompany().getName());
  }
}
