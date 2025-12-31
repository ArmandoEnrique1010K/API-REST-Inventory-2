package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.model.mapper.StockLotMapper;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;

@Service
public class StockLotServiceImpl implements StockLotService{

  @Autowired
  private StockLotRepository stockLotRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<StockLotListResponse> searchAllStockLotsByParams(
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable, 
      LocalDateTime minCreatedAt, 
      LocalDateTime maxCreatedAt, 
      String productName,
      Pageable pageable) {

        Page<StockLot> stockLots = stockLotRepository.findAllByParams(minQuantityAvailable, maxQuantityAvailable, minCreatedAt, maxCreatedAt, productName, pageable);

        return stockLots.map(stocklot -> StockLotMapper.builder().setStockLot(stocklot).buildStockLotListResponse());
      }

  @Override
  @Transactional(readOnly = true)
  public StockLotDetailsResponse findStockLotById(Long stockLotId) {
    if (stockLotId == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(stockLotId)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock no existe en el sistema"));

    return StockLotMapper.builder().setStockLot(stockLot).buildStockLotDetailsResponse();
  }
  
  // TODO: ESTE MÉTODO SE PODRIA UTILIZAR PARA ACTUALIZAR EL STOCK
  // @Override
  // public void sumAvailableQuantityByProductId(Long productId) {

  //   Product product = productRepository.findById(productId).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe"));

  //   int totalStock = stockLotRepository.sumAvailableByProductId(productId);
  //   product.setStock(totalStock);
  //   productRepository.save(product);

  // }
  
}
