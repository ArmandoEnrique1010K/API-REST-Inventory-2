package com.pe.inventoryapp.backend.deliveryline.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.mapper.StockLot_DeliveryLineMapper;
import com.pe.inventoryapp.backend.deliveryline.model.response.StockLot_DeliveryLineResponse;
import com.pe.inventoryapp.backend.deliveryline.repository.StockLot_DeliveryLineRepository;

@Service
public class StockLot_DeliveryLineServiceImpl implements StockLot_DeliveryLineService{

  private final StockLot_DeliveryLineRepository stockLotDeliveryLineRepository;

  public StockLot_DeliveryLineServiceImpl (StockLot_DeliveryLineRepository stockLotDeliveryLineRepository){
    this.stockLotDeliveryLineRepository = stockLotDeliveryLineRepository;
  }

  @Override
  public List<StockLot_DeliveryLineResponse> findAllByDeliveryLineId(Long id) {
    List<StockLot_DeliveryLine> stockLotDeliveryLines = (List<StockLot_DeliveryLine>) stockLotDeliveryLineRepository.findAllByDeliveryLineId(id);

    return stockLotDeliveryLines.stream().map(stockLotDeliveryLine -> StockLot_DeliveryLineMapper.builder().setStockLot_DeliveryLine(stockLotDeliveryLine).buildStockLot_DeliveryLineResponse()).collect(Collectors.toList());
  }
  
}
