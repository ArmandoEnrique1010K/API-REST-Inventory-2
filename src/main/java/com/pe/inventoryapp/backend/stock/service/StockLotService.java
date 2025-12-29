package com.pe.inventoryapp.backend.stock.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.stock.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stock.model.response.StockLotListResponse;

public interface StockLotService {
  Page<StockLotListResponse> searchAllStockLotsByParams(
    Integer minQuantityAvailable,
    Integer maxQuantityAvailable,
    LocalDateTime minCreatedAt,
    LocalDateTime maxCreatedAt,
    String productName,
    Pageable pageable
  );

  StockLotDetailsResponse findStockLotById(Long stockLotId);
  // void sumAvailableQuantityByProductId(Long productId);
}
