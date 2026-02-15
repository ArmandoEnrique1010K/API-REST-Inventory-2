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
      Integer minQuantityReceived,
      Integer maxQuantityReceived,
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      String keyword,
      Long companyId,
      Long categoryId,
      Long typeId,
      Pageable pageable);

  List<StockLotSameProductListResponse> findAllStockLotsExceptOneStockLotByModelId(Long modelId, Long companyId,
      Long stockLotId);

  StockLotDetailsResponse findStockLotById(Long idStockLot);

  void increaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void decreaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void recoveryStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user);

  void transferStockLot(Long idStockLotEmitter, StockLotTransferRequest stockLotTransferRequest, Long id_user);

  // void sumAvailableQuantityByProductId(Long productId);
}
