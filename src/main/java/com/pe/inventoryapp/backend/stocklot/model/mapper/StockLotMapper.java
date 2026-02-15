package com.pe.inventoryapp.backend.stocklot.model.mapper;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotSameProductListResponse;

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
        stockLot.getBatch(),
        stockLot.getQuantityAvailable(),
        stockLot.getCreatedAt(),
        stockLot.getModel().getId(),
        stockLot.getModel().getName(),
        stockLot.getModel().getImageUrl(),
        stockLot.getModel().getProduct().getId(),
        stockLot.getModel().getProduct().getName());
  }

  public StockLotDetailsResponse buildStockLotDetailsResponse() {
    if (stockLot == null) {
      throw new RuntimeException("Debe pasar la entidad Company");
    }

    return new StockLotDetailsResponse(
        stockLot.getId(),
        stockLot.getBatch(),
        stockLot.getQuantityReceived(),
        stockLot.getQuantityAvailable(),
        stockLot.getQuantityDelivered(),
        stockLot.getQuantityLost(),
        stockLot.getQuantityRecovered(),
        stockLot.isTemporary(),
        stockLot.getCreatedAt(),
        stockLot.getUpdatedAt(),

        stockLot.getModel().getId(),
        stockLot.getModel().getName(),
        stockLot.getModel().getImageUrl(),
        stockLot.getCompany().getId(),
        stockLot.getCompany().getName(),

        stockLot.getModel().getProduct().getId(),
        stockLot.getModel().getProduct().getName(),

        stockLot.getModel().getProduct().getType().getId(),
        stockLot.getModel().getProduct().getType().getName(),

        stockLot.getModel().getProduct().getCategory().getId(),
        stockLot.getModel().getProduct().getCategory().getName()
    );
  }

  public StockLotSameProductListResponse buildStockLotSameProductListResponse() {
    if (stockLot == null) {
      throw new RuntimeException("Debe pasar la entidad Company");
    }

    return new StockLotSameProductListResponse(
        stockLot.getId(),
        stockLot.getBatch(),
        stockLot.getQuantityAvailable(),
        stockLot.getCreatedAt());
  }
}
