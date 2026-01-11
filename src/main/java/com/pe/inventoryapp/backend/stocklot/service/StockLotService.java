package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotAdjustmentRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotReceiveRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotTransferRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotSameProductListResponse;

public interface StockLotService {
  void saveStockLot(StockLotReceiveRequest stockLotReceiveRequest, Long id_user);

  PageResponse<StockLotListResponse> searchAllStockLotsByParams(
    Integer minQuantityAvailable,
    Integer maxQuantityAvailable,
    Integer minQuantityReceived,
    Integer maxQuantityReceived,
    Integer minQuantityDelivered,
    Integer maxQuantityDelivered,
    LocalDateTime minCreatedAt,
    LocalDateTime maxCreatedAt,
    String productName,
    Boolean zeroStock,
    Long companyId,
    Pageable pageable
  );

  PageResponse<StockLotListResponse> searchAllStockLotsByParamsAndProductId(
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      Integer minQuantityReceived,
      Integer maxQuantityReceived,
      Integer minQuantityDelivered,
      Integer maxQuantityDelivered,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      Boolean zeroStock,
      Long companyId,
      Long productId,
      Pageable pageable);

  PageResponse<StockLotListResponse> searchAllStockLotsByNotZeroStockAndParams(
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      Long companyId,
      String productName,
      Pageable pageable);

  PageResponse<StockLotListResponse> searchAllStockLotsByNotZeroStockAndParamsAndProductId(
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      Long companyId,
      Long productId,
      Pageable pageable);

  List<StockLotSameProductListResponse> searchAllStockLotsExceptOneByProductId(Long stockLotId, Long productId);

  StockLotDetailsResponse findStockLotById(Long idStockLot);
  
  void increaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void decreaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void recoveryStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void transferStockLot(Long idStockLotEmitter, StockLotTransferRequest stockLotTransferRequest, Long id_user);

  // void sumAvailableQuantityByProductId(Long productId);
}
