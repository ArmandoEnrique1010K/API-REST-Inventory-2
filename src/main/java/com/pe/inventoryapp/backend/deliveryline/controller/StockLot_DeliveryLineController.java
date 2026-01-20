package com.pe.inventoryapp.backend.deliveryline.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.deliveryline.model.response.StockLot_DeliveryLineResponse;
import com.pe.inventoryapp.backend.deliveryline.service.StockLot_DeliveryLineService;

@RestController
@RequestMapping("/api/stock-lot-delivery-lines")
public class StockLot_DeliveryLineController {

  @Autowired
  private StockLot_DeliveryLineService stockLot_DeliveryLineService;

  @Autowired
  private ResponseService responseService;

  @GetMapping("/delivery-line/{id}")
  public ResponseEntity<?> getStockLotsByDeliveryLine(@PathVariable Long id) {
    List<StockLot_DeliveryLineResponse> stockLot_DeliveryLines = stockLot_DeliveryLineService.findAllByDeliveryLineId(id);
    DataResponse<List<StockLot_DeliveryLineResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        stockLot_DeliveryLines);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }
}