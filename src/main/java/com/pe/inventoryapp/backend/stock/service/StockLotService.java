package com.pe.inventoryapp.backend.stock.service;

public interface StockLotService {
  void sumAvailableQuantityByProductId(Long productId);
}
