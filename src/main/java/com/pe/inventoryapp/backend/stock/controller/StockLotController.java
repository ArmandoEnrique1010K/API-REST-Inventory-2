package com.pe.inventoryapp.backend.stock.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.stock.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stock.service.StockLotService;

@RestController
@RequestMapping("/api/stock-lots")
public class StockLotController {
  @Autowired
  private StockLotService stockLotService;

  // TODO: PROBAR ESTE ENDPOINT
  @GetMapping
  public ResponseEntity<?> listAllStockLots(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantityAvailable,
      @RequestParam(required = false) Integer maxQuantityAvailable,
      @RequestParam(required = false) LocalDateTime minCreatedAt,
      @RequestParam(required = false) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) String productName
    ) {

    Pageable pageable = PageRequest.of(page, 20);

    Page<StockLotListResponse> stockLots = stockLotService.searchAllStockLotsByParams(
        minQuantityAvailable, maxQuantityAvailable, minCreatedAt, maxCreatedAt, productName, pageable);

    return ResponseEntity.status(200).body(stockLots);
  }

}
